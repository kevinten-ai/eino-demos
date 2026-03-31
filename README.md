# Eino Learning Demos

> [CloudWeGo Eino](https://github.com/cloudwego/eino) 框架学习项目 — 字节跳动开源的 Go AI Agent 开发框架

[![Go](https://img.shields.io/badge/Go-1.22+-00ADD8?logo=go&logoColor=white)](https://go.dev/)
[![Eino](https://img.shields.io/badge/Eino-CloudWeGo-blue)](https://github.com/cloudwego/eino)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)

## 为什么选 Eino？

| 维度 | Eino | LangChainGo | Google ADK |
|------|------|-------------|------------|
| 设计哲学 | Go 原生，Graph 编排 | Python 移植 | Google 生态绑定 |
| 生产验证 | 字节跳动（豆包、TikTok） | 社区项目 | 较新 |
| Stars | 10k+ | 9k+ | 7k+ |
| 核心能力 | ReAct + Graph + ADK | Chains + Agents | A2A 协议 |

## Demo 总览

通过 **6 个渐进式 Demo**，从零掌握 Eino 核心能力：

```
01 基础对话        最简 LLM 调用，理解 Message 和 ChatModel
      ↓
02 工具调用        让 LLM 调用外部函数（Function Calling）
      ↓
03 ReAct Agent    自动"推理→行动→观察"循环，不用手写调度
      ↓
04 Graph 编排      Chain 链式 + Graph DAG 编排复杂流程
      ↓
05 流式输出        StreamReader 打字机效果，实时响应
      ↓
06 多 Agent 协作   ADK 流水线，多个 Agent 接力完成任务
```

| Demo | 主题 | 核心 API | 学习内容 |
|------|------|----------|----------|
| [`01-basic-chat`](examples/01-basic-chat/main.go) | 基础对话 | `ChatModel.Generate()` | Message 类型、多轮对话 |
| [`02-tool-calling`](examples/02-tool-calling/main.go) | 工具调用 | `utils.InferTool()` | Tool 定义、Function Calling 流程 |
| [`03-react-agent`](examples/03-react-agent/main.go) | ReAct Agent | `react.NewAgent()` | 自动推理-行动循环 |
| [`04-graph-workflow`](examples/04-graph-workflow/main.go) | Graph 编排 | `compose.NewGraph()` | Chain + Graph DAG + Lambda 节点 |
| [`05-streaming`](examples/05-streaming/main.go) | 流式输出 | `StreamReader.Recv()` | 打字机效果、Chain Stream |
| [`06-multi-agent`](examples/06-multi-agent/main.go) | 多 Agent 协作 | `adk.NewSequentialAgent()` | Agent 流水线编排 |

## 快速开始

### 环境要求

- Go 1.22+
- LLM API Key（支持 OpenAI / DashScope / 任何 OpenAI 兼容 API）

### 1. 克隆项目

```bash
git clone https://github.com/kevinten-ai/eino-demos.git
cd eino-demos
```

### 2. 配置 API Key

```bash
# 方式一：使用 DashScope（阿里云，默认）
export DASHSCOPE_API_KEY="your-api-key"

# 方式二：使用 OpenAI
export OPENAI_API_KEY="your-api-key"
export OPENAI_BASE_URL="https://api.openai.com/v1"

# 方式三：使用其他 OpenAI 兼容 API（如 DeepSeek、智谱等）
export OPENAI_API_KEY="your-api-key"
export OPENAI_BASE_URL="https://api.deepseek.com/v1"
export EINO_MODEL="deepseek-chat"
```

### 3. 运行 Demo

```bash
go mod tidy                            # 安装依赖
go run ./examples/01-basic-chat/       # 从第一个开始
```

## 项目结构

```
eino-demos/
├── examples/
│   ├── 01-basic-chat/       # ChatModel 基础对话
│   ├── 02-tool-calling/     # Tool 工具调用
│   ├── 03-react-agent/      # ReAct 推理-行动 Agent
│   ├── 04-graph-workflow/   # Graph/Chain 编排
│   ├── 05-streaming/        # 流式输出
│   └── 06-multi-agent/      # 多 Agent 协作
├── internal/
│   └── config/              # 公共配置（模型初始化）
├── go.mod
└── README.md
```

## Eino 架构概览

```
┌──────────────────────────────────────────────────┐
│                   Eino 架构                       │
├──────────────────────────────────────────────────┤
│                                                  │
│  Component 层（可复用组件）                        │
│  ├── ChatModel   — LLM 模型接口                   │
│  ├── Tool        — 工具 / 函数调用                 │
│  ├── Prompt      — 提示词模板（FString/GoTemplate） │
│  ├── Retriever   — 检索器（RAG）                   │
│  └── Embedding   — 向量化                         │
│                                                  │
│  Compose 层（编排引擎）          ← Demo 04 重点     │
│  ├── Chain       — 线性链式编排                    │
│  ├── Graph       — DAG 图编排（核心能力）           │
│  ├── Lambda      — 自定义函数节点                  │
│  └── Runnable    — 统一执行接口（Invoke/Stream）    │
│                                                  │
│  Agent 层（智能代理）            ← Demo 03/06 重点  │
│  ├── ReAct Agent — 推理-行动循环                   │
│  └── ADK         — Agent 开发套件                  │
│      ├── Sequential  — 顺序执行                    │
│      ├── Parallel    — 并行执行                    │
│      ├── Supervisor  — 监督者模式                  │
│      └── Loop        — 循环执行                    │
│                                                  │
└──────────────────────────────────────────────────┘
```

## 环境变量说明

| 变量 | 必填 | 默认值 | 说明 |
|------|------|--------|------|
| `DASHSCOPE_API_KEY` | 二选一 | - | 阿里云 DashScope API Key |
| `OPENAI_API_KEY` | 二选一 | - | OpenAI 或兼容 API Key |
| `OPENAI_BASE_URL` | 否 | DashScope 兼容地址 | API Base URL |
| `EINO_MODEL` | 否 | `qwen-plus` | 模型名称 |

## 参考资料

- [Eino GitHub](https://github.com/cloudwego/eino) — 框架源码
- [Eino 官方文档](https://www.cloudwego.io/docs/eino/overview/) — 概念和 API 参考
- [Eino 示例仓库](https://github.com/cloudwego/eino-examples) — 官方示例
- [Eino 扩展组件](https://github.com/cloudwego/eino-ext) — 模型/工具/检索器扩展
- [Go Wiki: AI](https://go.dev/wiki/AI) — Go 官方 AI 资源汇总

## License

MIT
