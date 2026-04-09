// Demo 09: MCP（Model Context Protocol）
// 学习目标: 了解 MCP 协议的基本概念，以及如何连接和调用 MCP Server
//
// 核心概念:
//   - MCP: Anthropic 提出的开放协议，用于标准化 LLM 与外部工具、数据源、系统的连接
//   - MCP Client: 连接 MCP Server，发现可用工具，执行工具调用
//   - Tool Definition: MCP Server 暴露的工具元信息（名称、描述、参数 schema）
//
// 注意: 本 Demo 需要一个运行中的 MCP Server（HTTP 接口）才能实际调用。
//       如果没有可用的 MCP Server，代码会展示发现和调用流程的完整逻辑。
package main

import (
	"context"
	"fmt"

	"github.com/kevinten-ai/eino-demos/pkg/mcp"
)

func main() {
	fmt.Println("=== MCP Demo ===")
	fmt.Println("MCP（Model Context Protocol）是连接 AI 与外部系统的标准协议\n")

	// 模拟一个 MCP Server 地址
	// 实际使用时，需要启动一个支持 MCP over HTTP 的服务
	serverURL := "http://localhost:3001"

	client := mcp.NewClient(serverURL)
	ctx := context.Background()

	fmt.Printf("正在连接 MCP Server: %s\n", serverURL)
	tools, err := client.DiscoverTools(ctx)
	if err != nil {
		fmt.Printf("发现工具失败: %v\n", err)
		fmt.Println("\n提示: 请确保有一个 MCP Server 在指定地址运行。")
		fmt.Println("你可以参考 https://modelcontextprotocol.io/ 搭建一个测试服务器。")
		return
	}

	fmt.Printf("发现 %d 个工具:\n", len(tools))
	for _, t := range tools {
		fmt.Printf("  - %s: %s\n", t.Name, t.Description)
	}

	// 如果发现了 weather 工具，尝试调用
	for _, t := range tools {
		if t.Name == "get_weather" {
			fmt.Println("\n调用工具: get_weather")
			result, err := client.CallTool(ctx, t.Name, map[string]any{
				"city": "北京",
			})
			if err != nil {
				fmt.Printf("调用失败: %v\n", err)
				return
			}
			fmt.Printf("工具返回: %s\n", result)
		}
	}
}
