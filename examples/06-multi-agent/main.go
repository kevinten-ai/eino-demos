// Demo 06: 多 Agent 协作
// 学习目标: 了解 Eino ADK 的多 Agent 编排能力
//
// 核心概念:
//   - adk.NewChatModelAgent: 创建基于 ChatModel 的 Agent（ADK 风格）
//   - adk.NewSequentialAgent: 顺序执行多个 Agent（流水线）
//   - adk.NewRunner: 创建 Agent 运行器
//   - runner.Query(): 发送用户查询，返回事件迭代器
//   - adk.GetMessage(): 从事件中提取消息
//
// 场景: 翻译流水线
//   Agent1（翻译员）→ Agent2（审校员）→ 最终结果
//   翻译员先翻译，审校员再润色校对
package main

import (
	"context"
	"fmt"

	"github.com/cloudwego/eino/adk"
	"github.com/cloudwego/eino/schema"
	"github.com/kevinten-ai/eino-demos/internal/config"
)

func main() {
	ctx := context.Background()
	chatModel := config.MustNewChatModel(ctx)

	fmt.Println("=== 多 Agent 协作 Demo ===")
	fmt.Println("场景: 翻译流水线（翻译员 → 审校员）\n")

	// ========== 创建 Agent 1: 翻译员 ==========
	translator, err := adk.NewChatModelAgent(ctx, &adk.ChatModelAgentConfig{
		Name:        "translator",
		Description: "将中文翻译为英文的专业翻译员",
		Instruction: `你是一个专业的中英翻译员。
请将用户提供的中文文本翻译为英文。
要求：
1. 翻译准确，语义忠实原文
2. 语言自然流畅
3. 只输出翻译结果，不要加额外说明`,
		Model: chatModel,
	})
	if err != nil {
		fmt.Printf("创建翻译 Agent 失败: %v\n", err)
		return
	}

	// ========== 创建 Agent 2: 审校员 ==========
	reviewer, err := adk.NewChatModelAgent(ctx, &adk.ChatModelAgentConfig{
		Name:        "reviewer",
		Description: "审校和润色英文翻译的专家",
		Instruction: `你是一个英文审校专家。
你会收到一段英文翻译，请：
1. 检查语法和拼写错误
2. 提升表达的地道性和流畅度
3. 输出格式：
   【润色后】<改进后的翻译>
   【修改说明】<简要说明改了什么，为什么>`,
		Model: chatModel,
	})
	if err != nil {
		fmt.Printf("创建审校 Agent 失败: %v\n", err)
		return
	}

	// ========== 创建顺序执行流水线 ==========
	// SequentialAgent: Agent1 的输出会作为 Agent2 的输入
	pipeline, err := adk.NewSequentialAgent(ctx, &adk.SequentialAgentConfig{
		Name:        "translation_pipeline",
		Description: "中译英流水线：先翻译，再审校润色",
		SubAgents:   []adk.Agent{translator, reviewer},
	})
	if err != nil {
		fmt.Printf("创建流水线失败: %v\n", err)
		return
	}

	// ========== 创建 Runner 并执行 ==========
	runner := adk.NewRunner(ctx, adk.RunnerConfig{
		Agent: pipeline,
	})

	// 要翻译的中文文本
	inputText := "云在青天水在瓶，心似莲花不染尘。世间万物皆有序，唯有真心最可亲。"
	fmt.Printf("输入: %s\n\n", inputText)

	// 执行查询
	iter := runner.Query(ctx, inputText)

	// 读取所有事件
	fmt.Println("--- 处理过程 ---")
	for {
		event, ok := iter.Next()
		if !ok {
			break
		}
		if event.Err != nil {
			fmt.Printf("错误: %v\n", event.Err)
			continue
		}

		// 从事件中提取消息
		msg, agentName, err := adk.GetMessage(event)
		if err != nil || msg == nil {
			continue
		}

		// 只显示 assistant 角色的消息（跳过 user 角色的转发）
		if msg.Role == schema.Assistant {
			fmt.Printf("[%s] %s\n\n", agentName, msg.Content)
		}
	}
}
