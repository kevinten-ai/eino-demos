// Demo 10: 可观测性（Observability）
// 学习目标: 了解 Eino 的 Callback 机制，实现 LLM 调用链的追踪和指标收集
//
// 核心概念:
//   - callbacks.Handler: Eino 的回调接口，在节点执行前后触发
//   - callbacks.AppendGlobalHandlers: 注册全局回调
//   - MetricsStore: 自定义指标存储，记录调用延迟、输入/输出 token 数、错误率
package main

import (
	"context"
	"fmt"
	"time"

	"github.com/cloudwego/eino/callbacks"
	"github.com/cloudwego/eino/schema"
	"github.com/kevinten-ai/eino-demos/internal/config"
	"github.com/kevinten-ai/eino-demos/pkg/observability"
)

func main() {
	ctx := context.Background()
	chatModel := config.MustNewChatModel(ctx)

	// 创建指标存储和追踪回调
	metricsStore := observability.NewMetricsStore()
	tracer := observability.NewTracingCallback(metricsStore)

	// 注册为全局回调（后续所有 ChatModel.Generate 都会被追踪）
	callbacks.AppendGlobalHandlers(tracer)

	fmt.Println("=== Observability Demo ===")
	fmt.Println("所有 LLM 调用将被自动追踪并记录指标\n")

	// 模拟多次调用
	questions := []string{
		"什么是 Go 语言？",
		"用一句话介绍 Eino 框架。",
		"AI Agent 和 LLM 有什么区别？",
	}

	for _, q := range questions {
		fmt.Printf("提问: %s\n", q)
		start := time.Now()
		resp, err := chatModel.Generate(ctx, []*schema.Message{
			schema.UserMessage(q),
		})
		if err != nil {
			fmt.Printf("错误: %v\n", err)
			continue
		}
		fmt.Printf("回答: %s (耗时: %dms)\n\n", resp.Content, time.Since(start).Milliseconds())
		// 稍微延迟，避免 API 限流
		time.Sleep(200 * time.Millisecond)
	}

	// 打印统计报告
	fmt.Println("--- 指标统计 ---")
	stats := metricsStore.Stats()
	for k, v := range stats {
		fmt.Printf("%s: %v\n", k, v)
	}

	fmt.Println("\n--- 最近调用记录 ---")
	recent := metricsStore.GetRecent(5)
	for i, call := range recent {
		status := "成功"
		if call.Error != "" {
			status = "失败: " + call.Error
		}
		fmt.Printf("%d. Model=%s Input=%d Output=%d Latency=%dms Status=%s\n",
			i+1, call.Model, call.InputLen, call.OutputLen, call.LatencyMs, status)
	}
}
