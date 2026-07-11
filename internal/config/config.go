package config

import (
	"context"
	"fmt"
	"os"

	"github.com/cloudwego/eino-ext/components/model/openai"
	"github.com/cloudwego/eino/components/model"
)

// MustNewChatModel 创建一个 OpenAI 兼容的 ChatModel
// 支持 OpenAI / DashScope / Ollama 等任何兼容 OpenAI API 的服务
func MustNewChatModel(ctx context.Context) model.ToolCallingChatModel {
	apiKey := os.Getenv("OPENAI_API_KEY")
	if apiKey == "" {
		apiKey = os.Getenv("DASHSCOPE_API_KEY")
	}
	if apiKey == "" {
		fmt.Println("警告: 未设置 OPENAI_API_KEY 或 DASHSCOPE_API_KEY，LLM 功能将不可用")
		apiKey = "placeholder"
	}

	baseURL := os.Getenv("OPENAI_BASE_URL")
	if baseURL == "" {
		baseURL = "https://dashscope.aliyuncs.com/compatible-mode/v1"
	}

	modelName := os.Getenv("EINO_MODEL")
	if modelName == "" {
		modelName = "qwen-plus"
	}

	chatModel, err := openai.NewChatModel(ctx, &openai.ChatModelConfig{
		BaseURL: baseURL,
		Model:   modelName,
		APIKey:  apiKey,
	})
	if err != nil {
		fmt.Printf("创建 ChatModel 失败: %v\n", err)
		os.Exit(1)
	}

	return chatModel
}
