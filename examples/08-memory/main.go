// Demo 08: 对话记忆（Memory）
// 学习目标: 了解 Agent 的记忆系统，实现多轮对话的上下文保持
//
// 核心概念:
//   - ConversationStore: 对话存储接口，管理消息历史
//   - InMemoryConversationStore: 内存实现（可替换为 Redis/DB）
//   - 多轮对话：将历史消息一同传给 LLM，让 AI 记住之前的上下文
package main

import (
	"context"
	"fmt"

	"github.com/cloudwego/eino/schema"
	"github.com/kevinten-ai/eino-demos/internal/config"
	"github.com/kevinten-ai/eino-demos/pkg/memory"
)

func main() {
	ctx := context.Background()
	chatModel := config.MustNewChatModel(ctx)
	store := memory.NewInMemoryConversationStore()
	sessionID := "user-001"

	fmt.Println("=== Memory Demo ===")
	fmt.Println("场景: 多轮对话，AI 能记住用户的名字和喜好\n")

	// 第一轮
	fmt.Println("[User] 你好，我叫小明，我喜欢吃川菜。")
	resp1 := chatWithMemory(ctx, chatModel, store, sessionID, "你好，我叫小明，我喜欢吃川菜。")
	fmt.Printf("[AI] %s\n\n", resp1.Content)

	// 第二轮（测试 AI 是否记住了名字）
	fmt.Println("[User] 我刚才说我叫什么名字？")
	resp2 := chatWithMemory(ctx, chatModel, store, sessionID, "我刚才说我叫什么名字？")
	fmt.Printf("[AI] %s\n\n", resp2.Content)

	// 第三轮（测试 AI 是否记住了喜好）
	fmt.Println("[User] 那我喜欢吃什么菜系？")
	resp3 := chatWithMemory(ctx, chatModel, store, sessionID, "那我喜欢吃什么菜系？")
	fmt.Printf("[AI] %s\n\n", resp3.Content)

	// 打印完整会话历史
	fmt.Println("--- 完整会话历史 ---")
	history := store.GetMessages(sessionID)
	for i, msg := range history {
		fmt.Printf("%d. [%s] %s\n", i+1, msg.Role, msg.Content)
	}
}

func chatWithMemory(ctx context.Context, chatModel interface {
	Generate(ctx context.Context, input []*schema.Message, opts ...interface{}) (*schema.Message, error)
}, store memory.ConversationStore, sessionID, userMsg string) *schema.Message {
	history := store.GetMessages(sessionID)
	msgs := append(history, schema.UserMessage(userMsg))
	resp, err := chatModel.Generate(ctx, msgs)
	if err != nil {
		fmt.Printf("生成失败: %v\n", err)
		return schema.AssistantMessage("抱歉，我出错了", nil)
	}
	store.AddMessages(sessionID, []*schema.Message{
		schema.UserMessage(userMsg),
		resp,
	})
	return resp
}
