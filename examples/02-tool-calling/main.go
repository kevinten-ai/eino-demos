// Demo 02: 工具调用 (Function Calling)
// 学习目标: 了解 Eino 的 Tool 系统，让 LLM 调用外部函数
//
// 核心概念:
//   - tool.InvokableTool: 工具接口，包含 Info() 和 InvokableRun()
//   - utils.InferTool: 从 Go 函数自动推断工具 schema（最简方式）
//   - schema.ToolInfo: 工具的元信息，描述名称、参数等
//   - model.WithTools: 将工具绑定到模型
package main

import (
	"context"
	"encoding/json"
	"fmt"
	"math"

	"github.com/cloudwego/eino/components/tool/utils"
	"github.com/cloudwego/eino/schema"
	"github.com/kevinten-ai/eino-demos/internal/config"
)

// ========== 方式一：用 InferTool 从 Go struct 自动推断 ==========

// WeatherInput 天气查询入参（jsonschema tag 用于生成工具描述）
type WeatherInput struct {
	City string `json:"city" jsonschema:"description=城市名称,required"`
}

// WeatherOutput 天气查询结果
type WeatherOutput struct {
	City    string `json:"city"`
	Weather string `json:"weather"`
	Temp    string `json:"temperature"`
}

func getWeather(_ context.Context, input *WeatherInput) (*WeatherOutput, error) {
	// 模拟天气查询
	data := map[string]WeatherOutput{
		"北京": {City: "北京", Weather: "晴", Temp: "22°C"},
		"上海": {City: "上海", Weather: "多云", Temp: "25°C"},
		"深圳": {City: "深圳", Weather: "阵雨", Temp: "28°C"},
	}
	if w, ok := data[input.City]; ok {
		return &w, nil
	}
	return &WeatherOutput{City: input.City, Weather: "未知", Temp: "N/A"}, nil
}

// ========== 方式二：用 InferTool + 简单函数 ==========

type CalcInput struct {
	Expression string `json:"expression" jsonschema:"description=数学表达式（如 sqrt/pow）,required"`
	A          float64 `json:"a" jsonschema:"description=第一个数,required"`
	B          float64 `json:"b" jsonschema:"description=第二个数（可选）"`
}

type CalcOutput struct {
	Result float64 `json:"result"`
}

func calculate(_ context.Context, input *CalcInput) (*CalcOutput, error) {
	var result float64
	switch input.Expression {
	case "add":
		result = input.A + input.B
	case "subtract":
		result = input.A - input.B
	case "multiply":
		result = input.A * input.B
	case "divide":
		if input.B == 0 {
			return nil, fmt.Errorf("除数不能为零")
		}
		result = input.A / input.B
	case "sqrt":
		result = math.Sqrt(input.A)
	case "pow":
		result = math.Pow(input.A, input.B)
	default:
		return nil, fmt.Errorf("不支持的运算: %s", input.Expression)
	}
	return &CalcOutput{Result: result}, nil
}

func main() {
	ctx := context.Background()
	chatModel := config.MustNewChatModel(ctx)

	// 创建工具（InferTool 自动从函数签名推断 JSON Schema）
	weatherTool, err := utils.InferTool("get_weather", "查询指定城市的天气", getWeather)
	if err != nil {
		panic(err)
	}
	calcTool, err := utils.InferTool("calculate", "数学计算器，支持 add/subtract/multiply/divide/sqrt/pow", calculate)
	if err != nil {
		panic(err)
	}

	// 获取工具的 schema 信息
	weatherInfo, _ := weatherTool.Info(ctx)
	calcInfo, _ := calcTool.Info(ctx)

	// 将工具绑定到模型
	modelWithTools, err := chatModel.WithTools([]*schema.ToolInfo{weatherInfo, calcInfo})
	if err != nil {
		panic(err)
	}

	fmt.Println("=== 工具调用 Demo ===")

	// 发送需要调用工具的请求
	messages := []*schema.Message{
		schema.SystemMessage("你是一个AI助手，可以查询天气和做数学计算。请根据用户需求调用对应的工具。"),
		schema.UserMessage("北京今天天气怎么样？另外帮我算一下 2 的 10 次方。"),
	}

	resp, err := modelWithTools.Generate(ctx, messages)
	if err != nil {
		fmt.Printf("生成失败: %v\n", err)
		return
	}

	// 检查模型是否要求调用工具
	if len(resp.ToolCalls) > 0 {
		fmt.Printf("模型请求调用 %d 个工具:\n", len(resp.ToolCalls))
		messages = append(messages, resp) // 将 assistant 的 tool_calls 消息加入历史

		for _, tc := range resp.ToolCalls {
			fmt.Printf("  - 工具: %s, 参数: %s\n", tc.Function.Name, tc.Function.Arguments)

			// 执行工具调用
			var toolResult string
			switch tc.Function.Name {
			case "get_weather":
				toolResult, _ = weatherTool.InvokableRun(ctx, tc.Function.Arguments)
			case "calculate":
				toolResult, _ = calcTool.InvokableRun(ctx, tc.Function.Arguments)
			}

			fmt.Printf("    结果: %s\n", toolResult)

			// 将工具结果作为 ToolMessage 返回给模型
			messages = append(messages, schema.ToolMessage(toolResult, tc.ID))
		}

		// 让模型根据工具结果生成最终回复
		finalResp, err := modelWithTools.Generate(ctx, messages)
		if err != nil {
			fmt.Printf("生成最终回复失败: %v\n", err)
			return
		}
		fmt.Printf("\nAI 最终回复: %s\n", finalResp.Content)
	} else {
		fmt.Printf("AI: %s\n", resp.Content)
	}

	// 打印工具 schema（学习用）
	fmt.Println("\n--- 工具 Schema ---")
	infoJSON, _ := json.MarshalIndent(weatherInfo, "", "  ")
	fmt.Printf("WeatherTool Schema:\n%s\n", infoJSON)
}
