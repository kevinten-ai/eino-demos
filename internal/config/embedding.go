package config

import (
	"context"
	"fmt"
	"os"

	"github.com/cloudwego/eino/components/embedding"
	openai_embedding "github.com/cloudwego/eino-ext/components/embedding/openai"
)

// MustNewEmbedder 创建 OpenAI 兼容的 Embedding 模型
func MustNewEmbedder(ctx context.Context) embedding.Embedder {
	apiKey := os.Getenv("OPENAI_API_KEY")
	if apiKey == "" {
		apiKey = os.Getenv("DASHSCOPE_API_KEY")
	}
	if apiKey == "" {
		fmt.Println("请设置环境变量 OPENAI_API_KEY 或 DASHSCOPE_API_KEY")
		os.Exit(1)
	}

	baseURL := os.Getenv("OPENAI_BASE_URL")
	if baseURL == "" {
		baseURL = "https://dashscope.aliyuncs.com/compatible-mode/v1"
	}

	modelName := os.Getenv("EINO_EMBEDDING_MODEL")
	if modelName == "" {
		modelName = "text-embedding-v3"
	}

	embedder, err := openai_embedding.NewEmbedder(ctx, &openai_embedding.EmbeddingConfig{
		BaseURL: baseURL,
		Model:   modelName,
		APIKey:  apiKey,
	})
	if err != nil {
		fmt.Printf("创建 Embedder 失败: %v\n", err)
		os.Exit(1)
	}

	return embedder
}
