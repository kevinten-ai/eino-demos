// Demo 03: ReAct Agent
// 学习目标: 了解 Eino 的 ReAct Agent，它能自动循环"推理-行动"直到完成任务
//
// 核心概念:
//   - react.NewAgent: 创建 ReAct 模式的 Agent
//   - react.AgentConfig: Agent 配置，包含模型、工具、最大步数
//   - compose.ToolsNodeConfig: 工具节点配置，管理工具集合
//   - 与 Demo 02 的区别：ReAct Agent 自动处理"调用工具 → 获取结果 → 继续推理"的循环
package main

import (
	"context"
	"fmt"
	"strings"
	"time"

	"github.com/cloudwego/eino/components/tool"
	"github.com/cloudwego/eino/components/tool/utils"
	"github.com/cloudwego/eino/compose"
	"github.com/cloudwego/eino/flow/agent/react"
	"github.com/cloudwego/eino/schema"
	"github.com/kevinten/eino-demos/internal/config"
)

// ========== 工具定义 ==========

type SearchInput struct {
	Query string `json:"query" jsonschema:"description=搜索关键词,required"`
}
type SearchOutput struct {
	Results []string `json:"results"`
}

func webSearch(_ context.Context, input *SearchInput) (*SearchOutput, error) {
	// 模拟搜索引擎
	mockData := map[string][]string{
		"eino": {
			"Eino 是字节跳动开源的 Go AI 应用开发框架",
			"Eino 支持 Graph 编排、ReAct Agent、流式处理",
			"Eino GitHub: github.com/cloudwego/eino，10k+ stars",
		},
		"go": {
			"Go 语言由 Google 开发，擅长并发编程",
			"Go 1.22 引入了增强的 for-range 语义",
		},
	}

	query := strings.ToLower(input.Query)
	for key, results := range mockData {
		if strings.Contains(query, key) {
			return &SearchOutput{Results: results}, nil
		}
	}
	return &SearchOutput{Results: []string{"未找到相关结果"}}, nil
}

type TimeInput struct {
	Timezone string `json:"timezone" jsonschema:"description=时区（如 Asia/Shanghai）"`
}
type TimeOutput struct {
	Time     string `json:"time"`
	Timezone string `json:"timezone"`
}

func getCurrentTime(_ context.Context, input *TimeInput) (*TimeOutput, error) {
	tz := input.Timezone
	if tz == "" {
		tz = "Asia/Shanghai"
	}
	loc, err := time.LoadLocation(tz)
	if err != nil {
		loc = time.FixedZone("CST", 8*3600)
	}
	now := time.Now().In(loc)
	return &TimeOutput{
		Time:     now.Format("2006-01-02 15:04:05"),
		Timezone: tz,
	}, nil
}

func main() {
	ctx := context.Background()
	chatModel := config.MustNewChatModel(ctx)

	// 创建工具
	searchTool, _ := utils.InferTool("web_search", "搜索网络信息", webSearch)
	timeTool, _ := utils.InferTool("get_current_time", "获取当前时间", getCurrentTime)

	// 创建 ReAct Agent
	// ReAct = Reasoning + Acting，模型会自动循环：
	//   思考(Thought) → 行动(Action) → 观察(Observation) → 思考 → ...
	agent, err := react.NewAgent(ctx, &react.AgentConfig{
		ToolCallingModel: chatModel,
		ToolsConfig: compose.ToolsNodeConfig{
			Tools: []tool.BaseTool{searchTool, timeTool},
		},
		MaxStep: 10,
	})
	if err != nil {
		fmt.Printf("创建 Agent 失败: %v\n", err)
		return
	}

	fmt.Println("=== ReAct Agent Demo ===")
	fmt.Println("Agent 会自动推理并调用工具来回答问题\n")

	// 提一个需要多步推理的问题
	messages := []*schema.Message{
		schema.SystemMessage("你是一个智能助手。请先搜索信息，再综合回答用户的问题。回答要简洁。"),
		schema.UserMessage("请帮我搜索一下 Eino 框架是什么，顺便告诉我现在几点了。"),
	}

	// Agent 会自动处理工具调用循环
	resp, err := agent.Generate(ctx, messages)
	if err != nil {
		fmt.Printf("Agent 执行失败: %v\n", err)
		return
	}

	fmt.Printf("AI: %s\n", resp.Content)
}
