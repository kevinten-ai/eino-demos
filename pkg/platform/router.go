package platform

import (
	"errors"
	"fmt"
	"io"
	"net/http"
	"strings"

	"github.com/cloudwego/eino/adk"
	"github.com/cloudwego/eino/components/prompt"
	"github.com/cloudwego/eino/components/tool"
	"github.com/cloudwego/eino/components/tool/utils"
	"github.com/cloudwego/eino/compose"
	"github.com/cloudwego/eino/flow/agent/react"
	"github.com/cloudwego/eino/schema"
	"github.com/gin-gonic/gin"
	"github.com/kevinten-ai/eino-demos/pkg/mcp"
)

func (s *Server) setupRoutes() {
	s.router.StaticFile("/", "./web/index.html")
	s.router.GET("/health", func(c *gin.Context) {
		c.JSON(200, gin.H{"status": "ok"})
	})

	api := s.router.Group("/api/v1")
	{
		api.POST("/chat", s.handleChat)
		api.POST("/tools", s.handleTools)
		api.POST("/react", s.handleReact)
		api.POST("/graph", s.handleGraph)
		api.POST("/stream", s.handleStream)
		api.POST("/multi-agent", s.handleMultiAgent)
		api.POST("/rag/upload", s.handleRAGUpload)
		api.POST("/rag/query", s.handleRAGQuery)
		api.POST("/memory/chat", s.handleMemoryChat)
		api.POST("/mcp/search", s.handleMCPSearch)
		api.GET("/metrics", s.handleMetrics)
	}
}

// ========== 01 基础对话 ==========
func (s *Server) handleChat(c *gin.Context) {
	var req struct {
		Messages []messageItem `json:"messages" binding:"required"`
	}
	if err := c.ShouldBindJSON(&req); err != nil {
		c.JSON(400, gin.H{"error": err.Error()})
		return
	}
	msgs := make([]*schema.Message, 0, len(req.Messages))
	for _, m := range req.Messages {
		msgs = append(msgs, buildMessage(m))
	}
	resp, err := s.chatModel.Generate(c.Request.Context(), msgs)
	if err != nil {
		c.JSON(500, gin.H{"error": err.Error()})
		return
	}
	c.JSON(200, gin.H{"role": "assistant", "content": resp.Content})
}

// ========== 02 工具调用 ==========
func (s *Server) handleTools(c *gin.Context) {
	type calcInput struct {
		Expression string  `json:"expression" jsonschema:"description=数学表达式,required"`
		A          float64 `json:"a" jsonschema:"description=第一个数,required"`
		B          float64 `json:"b" jsonschema:"description=第二个数"`
	}
	type calcOutput struct {
		Result float64 `json:"result"`
	}

	calcTool, _ := utils.InferTool("calculate", "数学计算器", func(ctx interface{}, input *calcInput) (*calcOutput, error) {
		return &calcOutput{Result: input.A + input.B}, nil
	})
	info, _ := calcTool.Info(c.Request.Context())
	modelWithTools, _ := s.chatModel.WithTools([]*schema.ToolInfo{info})

	var req struct {
		Question string `json:"question" binding:"required"`
	}
	if err := c.ShouldBindJSON(&req); err != nil {
		c.JSON(400, gin.H{"error": err.Error()})
		return
	}

	msgs := []*schema.Message{
		schema.SystemMessage("你是一个助手，可以使用计算器。"),
		schema.UserMessage(req.Question),
	}
	resp, err := modelWithTools.Generate(c.Request.Context(), msgs)
	if err != nil {
		c.JSON(500, gin.H{"error": err.Error()})
		return
	}
	c.JSON(200, gin.H{"content": resp.Content, "tool_calls": resp.ToolCalls})
}

// ========== 03 ReAct Agent ==========
func (s *Server) handleReact(c *gin.Context) {
	weatherTool, _ := utils.InferTool("get_weather", "查询天气", func(_ interface{}, input *struct {
		City string `json:"city" jsonschema:"description=城市,required"`
	}) (*struct{ Weather string `json:"weather"` }, error) {
		return &struct{ Weather string `json:"weather"` }{Weather: "晴天 25°C"}, nil
	})
	agent, err := react.NewAgent(c.Request.Context(), &react.AgentConfig{
		ToolCallingModel: s.chatModel,
		ToolsConfig: compose.ToolsNodeConfig{
			Tools: []tool.BaseTool{weatherTool},
		},
		MaxStep: 10,
	})
	if err != nil {
		c.JSON(500, gin.H{"error": err.Error()})
		return
	}
	var req struct {
		Question string `json:"question" binding:"required"`
	}
	if err := c.ShouldBindJSON(&req); err != nil {
		c.JSON(400, gin.H{"error": err.Error()})
		return
	}
	resp, err := agent.Generate(c.Request.Context(), []*schema.Message{
		schema.UserMessage(req.Question),
	})
	if err != nil {
		c.JSON(500, gin.H{"error": err.Error()})
		return
	}
	c.JSON(200, gin.H{"content": resp.Content})
}

// ========== 04 Graph 编排 ==========
func (s *Server) handleGraph(c *gin.Context) {
	var req struct {
		Role     string `json:"role" binding:"required"`
		Question string `json:"question" binding:"required"`
	}
	if err := c.ShouldBindJSON(&req); err != nil {
		c.JSON(400, gin.H{"error": err.Error()})
		return
	}

	tpl := prompt.FromMessages(schema.FString,
		schema.SystemMessage("你是一个{role}。"),
		schema.UserMessage("{question}"),
	)
	chain := compose.NewChain[map[string]any, *schema.Message]()
	chain.AppendChatTemplate(tpl)
	chain.AppendChatModel(s.chatModel)

	runnable, err := chain.Compile(c.Request.Context())
	if err != nil {
		c.JSON(500, gin.H{"error": err.Error()})
		return
	}
	resp, err := runnable.Invoke(c.Request.Context(), map[string]any{
		"role":     req.Role,
		"question": req.Question,
	})
	if err != nil {
		c.JSON(500, gin.H{"error": err.Error()})
		return
	}
	c.JSON(200, gin.H{"content": resp.Content})
}

// ========== 05 流式输出 ==========
func (s *Server) handleStream(c *gin.Context) {
	var req struct {
		Question string `json:"question" binding:"required"`
	}
	if err := c.ShouldBindJSON(&req); err != nil {
		c.JSON(400, gin.H{"error": err.Error()})
		return
	}

	c.Header("Content-Type", "text/event-stream")
	c.Header("Cache-Control", "no-cache")
	c.Header("Connection", "keep-alive")

	stream, err := s.chatModel.Stream(c.Request.Context(), []*schema.Message{
		schema.UserMessage(req.Question),
	})
	if err != nil {
		c.String(500, "error: "+err.Error())
		return
	}
	defer stream.Close()

	c.Stream(func(w io.Writer) bool {
		chunk, err := stream.Recv()
		if errors.Is(err, io.EOF) {
			fmt.Fprintf(w, "data: [DONE]\n\n")
			return false
		}
		if err != nil {
			fmt.Fprintf(w, "data: [ERROR] %s\n\n", err.Error())
			return false
		}
		fmt.Fprintf(w, "data: %s\n\n", chunk.Content)
		return true
	})
}

// ========== 06 多 Agent 协作 ==========
func (s *Server) handleMultiAgent(c *gin.Context) {
	var req struct {
		Text string `json:"text" binding:"required"`
	}
	if err := c.ShouldBindJSON(&req); err != nil {
		c.JSON(400, gin.H{"error": err.Error()})
		return
	}

	ctx := c.Request.Context()
	translator, _ := adk.NewChatModelAgent(ctx, &adk.ChatModelAgentConfig{
		Name:        "translator",
		Description: "翻译员",
		Instruction: "将中文翻译为英文，只输出翻译结果。",
		Model:       s.chatModel,
	})
	reviewer, _ := adk.NewChatModelAgent(ctx, &adk.ChatModelAgentConfig{
		Name:        "reviewer",
		Description: "审校员",
		Instruction: "润色并改进这段英文翻译。输出【润色后】+ 改进内容。",
		Model:       s.chatModel,
	})
	pipeline, _ := adk.NewSequentialAgent(ctx, &adk.SequentialAgentConfig{
		Name:        "translation_pipeline",
		Description: "翻译流水线",
		SubAgents:   []adk.Agent{translator, reviewer},
	})
	runner := adk.NewRunner(ctx, adk.RunnerConfig{Agent: pipeline})
	iter := runner.Query(ctx, req.Text)

	var steps []gin.H
	for {
		event, ok := iter.Next()
		if !ok {
			break
		}
		if event.Err != nil {
			steps = append(steps, gin.H{"error": event.Err.Error()})
			continue
		}
		msg, agentName, _ := adk.GetMessage(event)
		if msg != nil && msg.Role == schema.Assistant {
			steps = append(steps, gin.H{"agent": agentName, "content": msg.Content})
		}
	}
	c.JSON(200, gin.H{"steps": steps})
}

// ========== 07 RAG ==========
func (s *Server) handleRAGUpload(c *gin.Context) {
	var req struct {
		Documents []string `json:"documents" binding:"required"`
	}
	if err := c.ShouldBindJSON(&req); err != nil {
		c.JSON(400, gin.H{"error": err.Error()})
		return
	}
	if err := s.ragEngine.AddDocuments(c.Request.Context(), req.Documents); err != nil {
		c.JSON(500, gin.H{"error": err.Error()})
		return
	}
	c.JSON(200, gin.H{"status": "uploaded", "count": len(req.Documents)})
}

func (s *Server) handleRAGQuery(c *gin.Context) {
	var req struct {
		Question string `json:"question" binding:"required"`
	}
	if err := c.ShouldBindJSON(&req); err != nil {
		c.JSON(400, gin.H{"error": err.Error()})
		return
	}
	answer, err := s.ragEngine.Query(c.Request.Context(), req.Question)
	if err != nil {
		c.JSON(500, gin.H{"error": err.Error()})
		return
	}
	c.JSON(200, gin.H{"answer": answer})
}

// ========== 08 Memory ==========
func (s *Server) handleMemoryChat(c *gin.Context) {
	var req struct {
		SessionID string `json:"session_id" binding:"required"`
		Message   string `json:"message" binding:"required"`
	}
	if err := c.ShouldBindJSON(&req); err != nil {
		c.JSON(400, gin.H{"error": err.Error()})
		return
	}

	ctx := c.Request.Context()
	history := s.conversation.GetMessages(req.SessionID)
	history = append(history, schema.UserMessage(req.Message))

	resp, err := s.chatModel.Generate(ctx, history)
	if err != nil {
		c.JSON(500, gin.H{"error": err.Error()})
		return
	}
	s.conversation.AddMessages(req.SessionID, []*schema.Message{
		schema.UserMessage(req.Message),
		resp,
	})
	c.JSON(200, gin.H{"content": resp.Content, "session_id": req.SessionID})
}

// ========== 09 MCP ==========
func (s *Server) handleMCPSearch(c *gin.Context) {
	var req struct {
		ServerURL string `json:"server_url" binding:"required"`
		Query     string `json:"query" binding:"required"`
	}
	if err := c.ShouldBindJSON(&req); err != nil {
		c.JSON(400, gin.H{"error": err.Error()})
		return
	}

	client := mcp.NewClient(req.ServerURL)
	tools, err := client.DiscoverTools(c.Request.Context())
	if err != nil {
		c.JSON(500, gin.H{"error": err.Error()})
		return
	}

	var names []string
	for _, t := range tools {
		names = append(names, t.Name)
	}
	c.JSON(200, gin.H{"server": req.ServerURL, "tools": names, "query": req.Query})
}

// ========== 10 Observability ==========
func (s *Server) handleMetrics(c *gin.Context) {
	c.JSON(200, gin.H{
		"stats":   s.metrics.Stats(),
		"recent":  s.metrics.GetRecent(20),
	})
}

// helpers
type messageItem struct {
	Role    string `json:"role"`
	Content string `json:"content"`
}

func buildMessage(m messageItem) *schema.Message {
	switch strings.ToLower(m.Role) {
	case "system":
		return schema.SystemMessage(m.Content)
	case "assistant":
		return schema.AssistantMessage(m.Content, nil)
	default:
		return schema.UserMessage(m.Content)
	}
}
