// Demo 01: 基础对话
// 学习目标: 了解 Eino 的 ChatModel 接口，实现最简单的 LLM 对话
//
// 核心概念:
//   - schema.Message: Eino 的消息类型，包含 role 和 content
//   - model.ChatModel: LLM 模型接口，提供 Generate 和 Stream 方法
//   - openai.NewChatModel: 创建 OpenAI 兼容的模型实例（支持 DashScope 等）
package main

import (
	"context"
	"fmt"

	"github.com/cloudwego/eino/schema"
	"github.com/kevinten-ai/eino-demos/internal/config"
)

func main() {
	ctx := context.Background()
	chatModel := config.MustNewChatModel(ctx)

	// 构造消息列表（多轮对话）
	messages := []*schema.Message{
		schema.SystemMessage("你是一个友好的AI助手，回答简洁明了。"),
		schema.UserMessage("你好！请用一句话介绍一下 Go 语言的优势。"),
	}

	// 调用模型生成回复
	resp, err := chatModel.Generate(ctx, messages)
	if err != nil {
		fmt.Printf("生成失败: %v\n", err)
		return
	}

	fmt.Println("=== 基础对话 Demo ===")
	fmt.Printf("AI: %s\n", resp.Content)

	// 多轮对话：将 AI 回复加入历史，继续提问
	messages = append(messages, resp)
	messages = append(messages, schema.UserMessage("那 Go 语言最适合做什么类型的项目？"))

	resp2, err := chatModel.Generate(ctx, messages)
	if err != nil {
		fmt.Printf("生成失败: %v\n", err)
		return
	}

	fmt.Printf("\nAI（第二轮）: %s\n", resp2.Content)
}
