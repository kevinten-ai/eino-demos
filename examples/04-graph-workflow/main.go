// Demo 04: Graph 编排工作流
// 学习目标: 了解 Eino 的 Graph 编排能力，用 DAG 组织复杂的处理流程
//
// 核心概念:
//   - compose.NewGraph: 创建有向无环图（DAG），定义数据处理流程
//   - compose.NewChain: 创建线性链式流程（Graph 的简化版）
//   - compose.START / compose.END: 图的起始和结束节点
//   - AddChatTemplateNode: 添加 Prompt 模板节点
//   - AddChatModelNode: 添加 LLM 模型节点
//   - AddLambdaNode: 添加自定义处理节点
//   - Compile(): 编译图为可执行的 Runnable
//
// 本 Demo 演示两种模式：
//   1. Chain（链式）：template → model（简单直线流程）
//   2. Graph（图式）：带分支和合并的复杂流程
package main

import (
	"context"
	"fmt"
	"strings"

	"github.com/cloudwego/eino/components/model"
	"github.com/cloudwego/eino/components/prompt"
	"github.com/cloudwego/eino/compose"
	"github.com/cloudwego/eino/schema"
	"github.com/kevinten-ai/eino-demos/internal/config"
)

func main() {
	ctx := context.Background()
	chatModel := config.MustNewChatModel(ctx)

	fmt.Println("=== Graph 编排工作流 Demo ===\n")

	// ========== 示例 1: Chain（线性链） ==========
	fmt.Println("--- 示例 1: Chain 链式编排 ---")
	chainDemo(ctx, chatModel)

	// ========== 示例 2: Graph（DAG 图编排） ==========
	fmt.Println("\n--- 示例 2: Graph 图编排 ---")
	graphDemo(ctx, chatModel)
}

// chainDemo 演示最简单的 Chain 模式：Prompt → Model
func chainDemo(ctx context.Context, chatModel model.ToolCallingChatModel) {
	// 创建 Prompt 模板（FString 格式，用 {variable} 占位）
	tpl := prompt.FromMessages(schema.FString,
		schema.SystemMessage("你是一个{role}，请用{style}的方式回答问题。"),
		schema.UserMessage("{question}"),
	)

	// 构建 Chain: template → model
	chain := compose.NewChain[map[string]any, *schema.Message]()
	chain.AppendChatTemplate(tpl)
	chain.AppendChatModel(chatModel)

	// 编译并执行
	runnable, err := chain.Compile(ctx)
	if err != nil {
		fmt.Printf("编译失败: %v\n", err)
		return
	}

	result, err := runnable.Invoke(ctx, map[string]any{
		"role":     "诗人",
		"style":    "五言绝句",
		"question": "描写春天",
	})
	if err != nil {
		fmt.Printf("执行失败: %v\n", err)
		return
	}

	fmt.Printf("Chain 输出:\n%s\n", result.Content)
}

// graphDemo 演示 Graph 模式：带 Lambda 节点的复杂流程
// 流程: START → preprocess(Lambda) → prompt(Template) → model → postprocess(Lambda) → END
func graphDemo(ctx context.Context, chatModel model.ToolCallingChatModel) {
	g := compose.NewGraph[map[string]any, string]()

	// 节点 1: 预处理（Lambda 节点 — 自定义 Go 函数）
	preprocess := compose.InvokableLambda(func(ctx context.Context, input map[string]any) (map[string]any, error) {
		// 对输入做预处理
		question := input["question"].(string)
		input["question"] = strings.TrimSpace(question)
		input["language"] = "中文"
		fmt.Printf("  [预处理] 问题: %s\n", input["question"])
		return input, nil
	})

	// 节点 2: Prompt 模板
	tpl := prompt.FromMessages(schema.FString,
		schema.SystemMessage("你是一个技术专家，请用{language}简洁回答。"),
		schema.UserMessage("{question}"),
	)

	// 节点 3: 后处理（Lambda 节点）
	postprocess := compose.InvokableLambda(func(ctx context.Context, msg *schema.Message) (string, error) {
		// 对输出做后处理
		result := fmt.Sprintf("【AI 回答】\n%s\n【字数: %d】", msg.Content, len([]rune(msg.Content)))
		return result, nil
	})

	// 构建图：定义节点和边
	_ = g.AddLambdaNode("preprocess", preprocess)
	_ = g.AddChatTemplateNode("prompt", tpl)
	_ = g.AddChatModelNode("model", chatModel)
	_ = g.AddLambdaNode("postprocess", postprocess)

	// 连接边：START → preprocess → prompt → model → postprocess → END
	_ = g.AddEdge(compose.START, "preprocess")
	_ = g.AddEdge("preprocess", "prompt")
	_ = g.AddEdge("prompt", "model")
	_ = g.AddEdge("model", "postprocess")
	_ = g.AddEdge("postprocess", compose.END)

	// 编译并执行
	runnable, err := g.Compile(ctx)
	if err != nil {
		fmt.Printf("编译失败: %v\n", err)
		return
	}

	result, err := runnable.Invoke(ctx, map[string]any{
		"question": "  什么是 Eino 框架？  ",
	})
	if err != nil {
		fmt.Printf("执行失败: %v\n", err)
		return
	}

	fmt.Println(result)
}
