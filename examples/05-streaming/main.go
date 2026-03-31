// Demo 05: 流式输出 (Streaming)
// 学习目标: 了解 Eino 的流式处理机制，实现打字机效果的实时输出
//
// 核心概念:
//   - model.Stream(): 流式生成，返回 StreamReader
//   - schema.StreamReader[T]: 通用流式读取器，用 Recv() 逐块读取
//   - schema.Pipe[T](): 创建流式管道（StreamReader + StreamWriter 对）
//   - Runnable.Stream(): Graph/Chain 的流式执行模式
//   - io.EOF: 流结束的信号
package main

import (
	"context"
	"errors"
	"fmt"
	"io"

	"github.com/cloudwego/eino/components/model"
	"github.com/cloudwego/eino/components/prompt"
	"github.com/cloudwego/eino/compose"
	"github.com/cloudwego/eino/schema"
	"github.com/kevinten/eino-demos/internal/config"
)

func main() {
	ctx := context.Background()
	chatModel := config.MustNewChatModel(ctx)

	fmt.Println("=== 流式输出 Demo ===\n")

	// ========== 示例 1: 直接模型流式输出 ==========
	fmt.Println("--- 示例 1: 模型直接 Stream ---")
	directStream(ctx, chatModel)

	// ========== 示例 2: Chain 流式输出 ==========
	fmt.Println("\n--- 示例 2: Chain 流式编排 ---")
	chainStream(ctx, chatModel)
}

// directStream 直接使用 ChatModel.Stream()
func directStream(ctx context.Context, chatModel model.ToolCallingChatModel) {
	messages := []*schema.Message{
		schema.SystemMessage("你是一个讲故事的高手，故事要简短有趣。"),
		schema.UserMessage("请用 3 句话讲一个关于程序员和 AI 的小故事。"),
	}

	// Stream 返回一个 StreamReader，可以逐块读取
	sr, err := chatModel.Stream(ctx, messages)
	if err != nil {
		fmt.Printf("流式请求失败: %v\n", err)
		return
	}
	defer sr.Close()

	fmt.Print("AI: ")
	for {
		chunk, err := sr.Recv()
		if errors.Is(err, io.EOF) {
			break // 流结束
		}
		if err != nil {
			fmt.Printf("\n读取失败: %v\n", err)
			return
		}
		// 每收到一块就立即输出（打字机效果）
		fmt.Print(chunk.Content)
	}
	fmt.Println() // 换行
}

// chainStream 使用 Chain 的 Stream 模式
func chainStream(ctx context.Context, chatModel model.ToolCallingChatModel) {
	tpl := prompt.FromMessages(schema.FString,
		schema.SystemMessage("你是一个{role}。"),
		schema.UserMessage("{question}"),
	)

	chain := compose.NewChain[map[string]any, *schema.Message]()
	chain.AppendChatTemplate(tpl)
	chain.AppendChatModel(chatModel)

	runnable, err := chain.Compile(ctx)
	if err != nil {
		fmt.Printf("编译失败: %v\n", err)
		return
	}

	// 使用 Stream 模式执行 Chain
	sr, err := runnable.Stream(ctx, map[string]any{
		"role":     "Go 语言专家",
		"question": "用 3 个要点说明为什么 Go 适合写 Agent 框架",
	})
	if err != nil {
		fmt.Printf("流式执行失败: %v\n", err)
		return
	}
	defer sr.Close()

	fmt.Print("AI: ")
	for {
		chunk, err := sr.Recv()
		if errors.Is(err, io.EOF) {
			break
		}
		if err != nil {
			fmt.Printf("\n读取失败: %v\n", err)
			return
		}
		fmt.Print(chunk.Content)
	}
	fmt.Println()
}
