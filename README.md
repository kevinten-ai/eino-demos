# Eino Agent Platform

[![Go](https://img.shields.io/badge/Go-1.22+-00ADD8?logo=go&logoColor=white)](https://go.dev/)
[![Eino](https://img.shields.io/badge/Eino-CloudWeGo-blue)](https://github.com/cloudwego/eino)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)

> [CloudWeGo Eino](https://github.com/cloudwego/eino) 框架学习项目 — 字节跳动开源的 Go AI Agent 开发框架

## 项目定位

这是一个**渐进式学习 + 统一 Playground 平台**的项目：

- **10 个 CLI Demo**：从零开始学习 Eino 核心能力
- **1 个 Web Platform**：通过 REST API 和 Web UI 统一体验所有 Demo
- **生产级包结构**：`pkg/` 下是可复用的 Agent Platform 组件（RAG、Memory、Observability、MCP）

## 为什么选 Eino？

| 维度 | Eino | LangChainGo | Google ADK |
|------|------|-------------|------------|
| 设计哲学 | Go 原生，Graph 编排 | Python 移植 | Google 生态绑定 |
| 生产验证 | 字节跳动（豆包、TikTok） | 社区项目 | 较新 |
| Stars | 10k+ | 9k+ | 7k+ |
| 核心能力 | ReAct + Graph + ADK | Chains + Agents | A2A 协议 |

## Demo 总览

```
01 基础对话        ChatModel、Message、多轮对话
      ↓
02 工具调用        Function Calling（手动工具调度）
      ↓
03 ReAct Agent    自动"推理→行动→观察"循环
      ↓
04 Graph 编排      Chain 链式 + Graph DAG 编排
      ↓
05 流式输出        StreamReader、SSE 打字机效果
      ↓
06 多 Agent 协作   ADK SequentialAgent 流水线
      ↓
07 RAG            Embedding + Vector Store + 检索生成
      ↓
08 Memory         对话记忆、会话持久化
      ↓
09 MCP            Model Context Protocol 工具集成
      ↓
10 Observability  Callback 追踪、指标、成本监控
      ↓
Web Platform      统一 REST API + Playground UI
```

## 快速开始

### 环境要求

- Go 1.22+
- LLM API Key（OpenAI / DashScope / 任意 OpenAI 兼容 API）

### 配置环境变量

```bash
# 方式一：DashScope（阿里云，默认推荐国内用户）
export DASHSCOPE_API_KEY="your-api-key"

# 方式二：OpenAI
export OPENAI_API_KEY="your-api-key"
export OPENAI_BASE_URL="https://api.openai.com/v1"

# 方式三：其他兼容 API（如 DeepSeek）
export OPENAI_API_KEY="your-api-key"
export OPENAI_BASE_URL="https://api.deepseek.com/v1"
export EINO_MODEL="deepseek-chat"

# 指定 Embedding 模型（默认 text-embedding-v3，DashScope 兼容）
export EINO_EMBEDDING_MODEL="text-embedding-v3"
```

### 方式一：运行 Web Platform

```bash
# 安装依赖
go mod tidy

# 启动服务器
go run ./cmd/server

# 打开浏览器访问 http://localhost:8080
```

Web 平台提供 10 个 Demo 的交互式 UI：

| 端点 | 功能 |
|------|------|
| `GET /health` | 健康检查 |
| `POST /api/v1/chat` | 基础对话 |
| `POST /api/v1/tools` | 工具调用 |
| `POST /api/v1/react` | ReAct Agent |
| `POST /api/v1/graph` | Graph 编排 |
| `POST /api/v1/stream` | 流式输出（SSE） |
| `POST /api/v1/multi-agent` | 多 Agent 协作 |
| `POST /api/v1/rag/upload` | RAG 文档上传 |
| `POST /api/v1/rag/query` | RAG 问答 |
| `POST /api/v1/memory/chat` | 记忆对话 |
| `POST /api/v1/mcp/search` | MCP 工具发现 |
| `GET /api/v1/metrics` | 可观测性指标 |

### 方式二：单独运行 CLI Demo

```bash
go run ./examples/01-basic-chat/
go run ./examples/02-tool-calling/
go run ./examples/03-react-agent/
go run ./examples/04-graph-workflow/
go run ./examples/05-streaming/
go run ./examples/06-multi-agent/
go run ./examples/07-rag/
go run ./examples/08-memory/
go run ./examples/09-mcp/
go run ./examples/10-observability/
```

## 项目结构

```
eino-demos/
├── cmd/
│   └── server/              # 统一 Web Platform 入口
├── examples/
│   ├── 01-basic-chat/       # 基础对话
│   ├── 02-tool-calling/     # 工具调用
│   ├── 03-react-agent/      # ReAct Agent
│   ├── 04-graph-workflow/   # Graph 编排
│   ├── 05-streaming/        # 流式输出
│   ├── 06-multi-agent/      # 多 Agent 协作
│   ├── 07-rag/              # RAG 检索增强生成
│   ├── 08-memory/           # 对话记忆
│   ├── 09-mcp/              # MCP 协议
│   └── 10-observability/    # 可观测性
├── internal/
│   └── config/              # ChatModel / Embedder 配置
├── pkg/
│   ├── platform/            # Web 服务器、路由、Handler
│   ├── memory/              # 会话存储 + 向量存储
│   ├── rag/                 # RAG 引擎
│   ├── mcp/                 # MCP 客户端
│   └── observability/       # Callback 追踪 + Metrics
├── web/
│   └── index.html           # Playground UI
├── go.mod
└── README.md
```

## Eino 架构概览

```
┌────────────────────────────────────────────────────┐
│                   Eino 架构                         │
├────────────────────────────────────────────────────┤
│                                                     │
│  Component 层                                       │
│  ├── ChatModel   — LLM 模型接口（Demo 01）          │
│  ├── Tool        — 函数调用（Demo 02/03）           │
│  ├── Prompt      — 提示词模板（Demo 04）            │
│  ├── Embedding   — 文本向量化（Demo 07）            │
│  └── Retriever   — 检索器（Demo 07）                │
│                                                     │
│  Compose 层                                         │
│  ├── Chain       — 线性链式编排（Demo 04）          │
│  ├── Graph       — DAG 图编排（核心）               │
│  └── Lambda      — 自定义函数节点                   │
│                                                     │
│  Agent 层                                           │
│  ├── ReAct Agent — 推理-行动循环（Demo 03）         │
│  └── ADK         — Agent 开发套件（Demo 06）        │
│      ├── Sequential  — 顺序执行                     │
│      ├── Parallel    — 并行执行                     │
│      ├── Supervisor  — 监督者模式                   │
│      └── Loop        — 循环执行                     │
│                                                     │
│  Platform 能力                                      │
│  ├── Memory      — 对话记忆（Demo 08）              │
│  ├── RAG         — 检索增强（Demo 07）              │
│  ├── MCP         — 外部工具协议（Demo 09）          │
│  └── Observability — 追踪与指标（Demo 10）          │
│                                                     │
└────────────────────────────────────────────────────┘
```

## 各 Demo 核心 API

### 01 基础对话
```go
chatModel.Generate(ctx, []*schema.Message{
    schema.SystemMessage("你是一个助手"),
    schema.UserMessage("你好"),
})
```

### 02 工具调用
```go
tool, _ := utils.InferTool("calculate", "计算器", calcFn)
info, _ := tool.Info(ctx)
modelWithTools, _ := chatModel.WithTools([]*schema.ToolInfo{info})
```

### 03 ReAct Agent
```go
agent, _ := react.NewAgent(ctx, &react.AgentConfig{
    ToolCallingModel: chatModel,
    ToolsConfig:      compose.ToolsNodeConfig{Tools: tools},
    MaxStep:          10,
})
```

### 04 Graph 编排
```go
chain := compose.NewChain[map[string]any, *schema.Message]()
chain.AppendChatTemplate(tpl)
chain.AppendChatModel(chatModel)
runnable, _ := chain.Compile(ctx)
```

### 05 流式输出
```go
stream, _ := chatModel.Stream(ctx, messages)
for {
    chunk, err := stream.Recv()
    if errors.Is(err, io.EOF) { break }
    fmt.Print(chunk.Content)
}
```

### 06 多 Agent 协作
```go
pipeline, _ := adk.NewSequentialAgent(ctx, &adk.SequentialAgentConfig{
    SubAgents: []adk.Agent{translator, reviewer},
})
```

### 07 RAG
```go
engine := rag.NewEngine(embedder, chatModel)
engine.AddDocuments(ctx, docs)
answer, _ := engine.Query(ctx, question)
```

### 08 Memory
```go
store := memory.NewInMemoryConversationStore()
store.AddMessages(sessionID, messages)
history := store.GetMessages(sessionID)
```

### 09 MCP
```go
client := mcp.NewClient(serverURL)
tools, _ := client.DiscoverTools(ctx)
result, _ := client.CallTool(ctx, name, arguments)
```

### 10 Observability
```go
metrics := observability.NewMetricsStore()
tracer := observability.NewTracingCallback(metrics)
callbacks.AppendGlobalHandlers(tracer)
```

## 环境变量说明

| 变量 | 必填 | 默认值 | 说明 |
|------|------|--------|------|
| `DASHSCOPE_API_KEY` | 二选一 | - | DashScope API Key |
| `OPENAI_API_KEY` | 二选一 | - | OpenAI 兼容 API Key |
| `OPENAI_BASE_URL` | 否 | DashScope 地址 | 模型 API Base URL |
| `EINO_MODEL` | 否 | `qwen-plus` | 对话模型 |
| `EINO_EMBEDDING_MODEL` | 否 | `text-embedding-v3` | Embedding 模型 |
| `PORT` | 否 | `8080` | Web 服务端口 |

## 部署

### Railway（推荐）

```bash
# 安装 Railway CLI 并登录
npm i -g @railway/cli
railway login

# 连接项目并部署
railway link
railway up
```

### 本地构建

```bash
go build -o bin/server ./cmd/server
./bin/server
```

## 学习路径建议

```
第 1 周：框架基础
  - 跑通 Demo 01-06
  - 理解 ChatModel、Tool、Chain、Graph、Agent 的关系

第 2 周：平台能力
  - 跑通 Demo 07-10
  - 理解 RAG、Memory、MCP、Observability 的工程实现
  - 阅读 pkg/ 下的源码

第 3 周：系统架构
  - 启动 Web Platform（cmd/server）
  - 在 web/index.html 上添加新功能
  - 研究 Dify/FastGPT 的设计，思考平台级能力缺口
```

## 从 Demo 到 Agent Platform 还缺什么？

| 能力 | 状态 | 进阶方向 |
|------|------|----------|
| LLM 调用编排 | ✅ Demo 01-06 | 并发调度、模型路由、Fallback |
| 工具系统 | ⚠️ 基础版 | MCP 市场、安全沙箱、权限管控 |
| RAG | ⚠️ 内存版 | 生产级 Vector DB、重排序、Hybrid Search |
| Memory | ⚠️ 内存版 | Redis/DB 持久化、记忆总结、关键信息提取 |
| 可观测性 | ⚠️ 基础版 | OpenTelemetry、Langfuse、LLM 成本分摊 |
| 工作流持久化 | ❌ 未覆盖 | Temporal/Cadence 状态机、断点恢复 |
| 人机协同 | ❌ 未覆盖 | Human-in-the-loop、审批流、中断恢复 |
| 多租户安全 | ❌ 未覆盖 | 租户隔离、Prompt 注入防护、审计日志 |

## 参考资料

- [Eino GitHub](https://github.com/cloudwego/eino)
- [Eino 官方文档](https://www.cloudwego.io/docs/eino/overview/)
- [Eino 示例仓库](https://github.com/cloudwego/eino-examples)
- [Eino 扩展组件](https://github.com/cloudwego/eino-ext)
- [MCP 官方文档](https://modelcontextprotocol.io/)
- [Go Wiki: AI](https://go.dev/wiki/AI)

## License

MIT
