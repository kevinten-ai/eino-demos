# Eino Learning Demos

[CloudWeGo Eino](https://github.com/cloudwego/eino) 框架学习项目 — 字节跳动开源的 Go AI Agent 开发框架。

## 项目简介

通过 6 个渐进式 Demo，从零学习 Eino 的核心能力：

| Demo | 主题 | 学习内容 |
|------|------|----------|
| `01-basic-chat` | 基础对话 | ChatModel 接口、Message 类型、多轮对话 |
| `02-tool-calling` | 工具调用 | Tool 定义、InferTool、Function Calling 流程 |
| `03-react-agent` | ReAct Agent | 自动推理-行动循环、ToolsNodeConfig |
| `04-graph-workflow` | Graph 编排 | Chain 链式 / Graph 图式编排、Lambda 节点 |
| `05-streaming` | 流式输出 | StreamReader、打字机效果、Chain Stream |
| `06-multi-agent` | 多 Agent 协作 | ADK、SequentialAgent、Agent 流水线 |

## 快速开始

### 环境要求

- Go 1.22+
- LLM API Key（支持 OpenAI / DashScope / 任何 OpenAI 兼容 API）

### 配置

```bash
# 方式一：使用 DashScope（默认）
export DASHSCOPE_API_KEY="your-api-key"

# 方式二：使用 OpenAI
export OPENAI_API_KEY="your-api-key"
export OPENAI_BASE_URL="https://api.openai.com/v1"

# 可选：指定模型（默认 qwen-plus）
export EINO_MODEL="gpt-4o"
```

### 运行 Demo

```bash
# 安装依赖
go mod tidy

# 运行任意 Demo
go run ./examples/01-basic-chat/
go run ./examples/02-tool-calling/
go run ./examples/03-react-agent/
go run ./examples/04-graph-workflow/
go run ./examples/05-streaming/
go run ./examples/06-multi-agent/
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

## Eino 核心概念

```
┌─────────────────────────────────────────────┐
│                  Eino 架构                    │
├─────────────────────────────────────────────┤
│                                             │
│  Component 层（可复用组件）                    │
│  ├── ChatModel   — LLM 模型接口              │
│  ├── Tool        — 工具/函数调用              │
│  ├── Prompt      — 提示词模板                 │
│  ├── Retriever   — 检索器（RAG）              │
│  └── Embedding   — 向量化                    │
│                                             │
│  Compose 层（编排引擎）                       │
│  ├── Chain       — 线性链式编排               │
│  ├── Graph       — DAG 图编排（核心）          │
│  └── Lambda      — 自定义函数节点             │
│                                             │
│  Agent 层（智能代理）                         │
│  ├── ReAct Agent — 推理-行动循环              │
│  └── ADK         — Agent 开发套件             │
│      ├── Sequential  — 顺序执行               │
│      ├── Parallel    — 并行执行               │
│      ├── Supervisor  — 监督者模式             │
│      └── Loop        — 循环执行               │
│                                             │
└─────────────────────────────────────────────┘
```

## 参考资料

- [Eino GitHub](https://github.com/cloudwego/eino)
- [Eino 官方文档](https://www.cloudwego.io/docs/eino/overview/)
- [Eino 示例仓库](https://github.com/cloudwego/eino-examples)
- [Eino 扩展组件](https://github.com/cloudwego/eino-ext)

## License

MIT
