// Agent Platform Server
// 统一的 Agent Platform HTTP 服务，整合所有 Demo 为 REST API
package main

import (
	"context"
	"fmt"
	"log"
	"os"

	"github.com/kevinten-ai/eino-demos/pkg/platform"
)

func main() {
	ctx := context.Background()
	port := os.Getenv("PORT")
	if port == "" {
		port = "8080"
	}

	srv, err := platform.NewServer(ctx)
	if err != nil {
		log.Fatalf("创建服务器失败: %v", err)
	}

	fmt.Printf("Agent Platform 启动成功: http://localhost:%s\n", port)
	fmt.Println("可用端点:")
	fmt.Println("  GET  /health")
	fmt.Println("  POST /api/v1/chat")
	fmt.Println("  POST /api/v1/tools")
	fmt.Println("  POST /api/v1/react")
	fmt.Println("  POST /api/v1/graph")
	fmt.Println("  POST /api/v1/stream")
	fmt.Println("  POST /api/v1/multi-agent")
	fmt.Println("  POST /api/v1/rag/upload")
	fmt.Println("  POST /api/v1/rag/query")
	fmt.Println("  POST /api/v1/memory/chat")
	fmt.Println("  POST /api/v1/mcp/search")
	fmt.Println("  GET  /api/v1/metrics")
	fmt.Println("  GET  /")

	if err := srv.Run(":" + port); err != nil {
		log.Fatalf("服务器运行失败: %v", err)
	}
}
