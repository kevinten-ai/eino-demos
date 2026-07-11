package config

import (
	"context"
	"fmt"
	"os"
	"time"

	"github.com/cloudwego/eino-ext/components/embedding/dashscope"
	"github.com/cloudwego/eino/components/embedding"
)

type unavailableEmbedder struct {
	reason string
}

func (e *unavailableEmbedder) EmbedStrings(context.Context, []string, ...embedding.Option) ([][]float64, error) {
	return nil, fmt.Errorf("embedding unavailable: %s", e.reason)
}

// MustNewEmbedder creates the production DashScope embedder. In a no-key
// environment it returns an explicit unavailable implementation so health and
// static UI routes can start without silently generating invalid vectors.
func MustNewEmbedder(ctx context.Context) embedding.Embedder {
	apiKey := os.Getenv("DASHSCOPE_API_KEY")
	if apiKey == "" {
		fmt.Println("警告: 未设置 DASHSCOPE_API_KEY，RAG/embedding 功能不可用")
		return &unavailableEmbedder{reason: "DASHSCOPE_API_KEY is not set"}
	}

	modelName := os.Getenv("EINO_EMBEDDING_MODEL")
	if modelName == "" {
		modelName = "text-embedding-v3"
	}

	embedder, err := dashscope.NewEmbedder(ctx, &dashscope.EmbeddingConfig{
		APIKey:  apiKey,
		Model:   modelName,
		Timeout: 30 * time.Second,
	})
	if err != nil {
		fmt.Printf("创建 DashScope Embedder 失败: %v\n", err)
		return &unavailableEmbedder{reason: err.Error()}
	}
	return embedder
}
