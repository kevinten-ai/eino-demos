# Eino Agent Platform Working Guide

This repository is a Go learning and demo platform for CloudWeGo Eino.

## Scope

- Keep changes aligned with the existing Go package layout: `cmd/`, `examples/`, `internal/`, `pkg/`, and `web/`.
- Treat `server` as a local build artifact; do not commit it.
- Do not commit real API keys or local `.env` files. Use `.env.example` for placeholders.
- `go.sum` should be reviewed as dependency lock data when `go.mod` changes.
- CloudBase and Docker deployment files may be committed after confirming the target project.

## Validation

Prefer the narrowest useful checks:

```bash
go test ./...
go build ./cmd/server
```

For container/deployment changes, also validate the Docker build or CloudBase workflow before release.
