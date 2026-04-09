FROM golang:1.22-alpine AS builder
WORKDIR /app
COPY go.mod go.sum ./
RUN go mod download
COPY . .
RUN go build -o server ./cmd/server

FROM alpine:latest
WORKDIR /app
COPY --from=builder /app/server .
COPY --from=builder /app/web ./web
EXPOSE 8080
CMD ["./server"]
