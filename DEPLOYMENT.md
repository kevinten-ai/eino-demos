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

## Verification

Before handoff:

```bash
go test ./...
go build ./cmd/server
```

If Docker or CloudBase deployment changes are included, also validate the deployment path.
