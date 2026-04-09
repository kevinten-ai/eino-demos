package platform

import (
	"context"

	"github.com/cloudwego/eino/callbacks"
	"github.com/cloudwego/eino/components/model"
	"github.com/gin-gonic/gin"
	"github.com/kevinten-ai/eino-demos/internal/config"
	"github.com/kevinten-ai/eino-demos/pkg/memory"
	"github.com/kevinten-ai/eino-demos/pkg/observability"
	"github.com/kevinten-ai/eino-demos/pkg/rag"
)

// Server 平台服务器
type Server struct {
	router      *gin.Engine
	chatModel   model.ToolCallingChatModel
	metrics     *observability.MetricsStore
	tracer      *observability.TracingCallback
	conversation memory.ConversationStore
	ragEngine   *rag.Engine
}

func NewServer(ctx context.Context) (*Server, error) {
	gin.SetMode(gin.ReleaseMode)
	router := gin.Default()

	chatModel := config.MustNewChatModel(ctx)

	metrics := observability.NewMetricsStore()
	tracer := observability.NewTracingCallback(metrics)
	callbacks.AppendGlobalHandlers(tracer)

	embedder := config.MustNewEmbedder(ctx)
	ragEngine := rag.NewEngine(embedder, chatModel)
	conversationStore := memory.NewInMemoryConversationStore()

	s := &Server{
		router:       router,
		chatModel:    chatModel,
		metrics:      metrics,
		tracer:       tracer,
		conversation: conversationStore,
		ragEngine:    ragEngine,
	}
	s.setupRoutes()
	return s, nil
}

func (s *Server) Run(addr string) error {
	return s.router.Run(addr)
}
