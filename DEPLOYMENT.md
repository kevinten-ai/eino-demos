# Eino Agent Platform Deployment

This project exposes the Eino demo platform through `cmd/server` and can be deployed as a container.

## Runtime

- Language: Go 1.23+
- Server entry: `./cmd/server`
- Default local port: `8080`
- Container port in the current `Dockerfile`: `80`
- Static UI directory copied into the image: `web/`

## Environment

Set one provider key before enabling LLM-backed endpoints:

```bash
DASHSCOPE_API_KEY=
OPENAI_API_KEY=
OPENAI_BASE_URL=https://dashscope.aliyuncs.com/compatible-mode/v1
EINO_MODEL=qwen-plus
EINO_EMBEDDING_MODEL=text-embedding-v3
PORT=8080
```

The repository includes `.env.example` for placeholders only. Do not commit real `.env` files.

## Docker

```bash
docker build -t eino-demos .
docker run --rm -p 8080:80 --env-file .env eino-demos
```

The current image builds a static Go binary and exposes port `80` inside the container.

## CloudBase

`cloudbaserc.json` currently points to:

- envId: `ai-native-2gknzsob14f42138`
- cloudrun name: `eino-demos`

Confirm that this CloudBase environment is the intended deployment target before publishing.

### 2026-07-11 release status

- Target environment and service were confirmed through CloudBase CLI; `eino-demos` is a public container service.
- Deployment was attempted with source `.` and port `80` after local container verification.
- CloudBase rejected the release with `ResourceUnavailable.ResourceFrozen` because the environment was frozen after prolonged inactivity.
- Restore the resource through the CloudBase console's self-service prompt, then rerun the deployment. CloudBase requires a new deployment after unfreezing; an old version cannot simply be resumed.

## Verification

Before handoff:

```bash
go test ./...
go build ./cmd/server
```

If Docker or CloudBase deployment changes are included, also validate the deployment path.
