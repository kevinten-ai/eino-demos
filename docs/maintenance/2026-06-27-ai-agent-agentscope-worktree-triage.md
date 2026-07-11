# ai-agent-agentscope 工作区 Triage

- 日期: 2026-06-27
- 仓库: `kevinten-ai/eino-demos`
- 分支: `main`
- 用途: 在继续优化 Eino Agent Platform 前，先分类本地 Go 依赖、部署文件、构建产物和源码改动。

## 当前结论

- `go.mod` 已升级 Eino 相关依赖，`go.sum` 是配套依赖锁文件，提交前应一起审阅。
- `server` 是本地 Go 构建产物，已加入 `.gitignore`，不应提交。
- `.playwright-mcp/` 是本地浏览器工具缓存，已加入 `.gitignore`。
- `cloudbaserc.json` 指向 CloudBase 环境 `ai-native-2gknzsob14f42138` 和 Cloud Run 服务 `eino-demos`，发布前需确认该环境归属。
- `internal/config/embedding.go` 已接入 Eino 官方 DashScope embedder。无 Key 时返回明确错误，允许健康检查和静态页面启动，但不再生成随机向量。
- `internal/config/config.go` 在缺少 API key 时不再退出，而是使用占位值继续初始化；这便于无密钥环境启动页面，但 LLM 请求仍会失败。

## 变更分组

| 分组 | 路径/范围 | 状态 | 建议处理 |
|---|---|---|---|
| 维护规则 | `.gitignore`, `AGENTS.md`, `.env.example`, `DEPLOYMENT.md`, `docs/maintenance/2026-06-27-ai-agent-agentscope-worktree-triage.md` | 新增 | 作为 `maintenance-baseline` 提交。 |
| 依赖升级 | `go.mod`, `go.sum` | 修改/新增 | 结合 `go test ./...` 结果审阅后提交。 |
| 配置兼容 | `internal/config/config.go`, `internal/config/embedding.go` | 修改 | 确认 stub/占位 key 策略是否仅用于演示环境。 |
| 平台源码 | `pkg/memory/*`, `pkg/observability/callback.go`, `pkg/platform/router.go` | 修改 | 通过 Go 测试和手动接口验证后提交。 |
| 部署配置 | `Dockerfile`, `.dockerignore`, `cloudbaserc.json` | 修改/新增 | 确认 CloudBase 环境和容器端口。 |
| 本地生成物 | `server`, `.playwright-mcp/` | 已忽略 | 不提交。 |

## 推荐提交顺序

1. `maintenance-baseline`: `.gitignore`、`AGENTS.md`、`.env.example`、`DEPLOYMENT.md`、本 triage 文档。
2. `dependency-upgrade`: `go.mod`、`go.sum`。
3. `runtime-compat`: 配置、embedding stub、memory/vector/callback/router 兼容改动。
4. `deployment`: `Dockerfile`、`.dockerignore`、`cloudbaserc.json`。

## 后续检查

- 跑 `go test ./...` 和 `go build ./cmd/server`。
- 用空环境和真实 API key 环境分别验证 `/health`、基础 chat、RAG/embedding 路径。
- 用真实 DashScope Key 对 RAG 上传和查询路径执行生产冒烟测试。

## 验证记录

- `git -c core.quotePath=false status --short --ignored`: 已用于分类当前工作区。
- `git diff --check`: 已通过。
- 敏感信息文件级扫描: 新增/变更维护文档、Go 配置、部署文件和依赖文件未命中常见密钥模式。
- `go version`: 本轮使用 `go1.26.4 darwin/arm64`；项目最低版本为 Go 1.23。
- `go vet ./...`: 通过。
- `go test -race ./...`: 通过。
- `go build ./cmd/server`: 通过。
- `docker build -t eino-demos:local-check .`: 通过。
- 容器 `GET /health`: HTTP 200，响应 `{"status":"ok"}`。
- 无 Key 的 `POST /api/v1/rag/upload`: HTTP 500，并明确返回缺少 `DASHSCOPE_API_KEY`，未生成随机向量。
- CloudBase deploy: 目标服务确认无误，但平台返回 `ResourceUnavailable.ResourceFrozen`；需在控制台自助解冻后重新部署。
