FROM golang:1.23-alpine3.21 AS builder
RUN apk add --no-cache git
ENV GOPROXY=https://goproxy.cn,direct
WORKDIR /app
COPY go.mod go.sum ./
RUN go mod download
COPY . .
RUN CGO_ENABLED=0 go build -ldflags="-s -w" -o server ./cmd/server

FROM alpine:3.21
RUN apk add --no-cache ca-certificates
WORKDIR /app
ENV PORT=80
COPY --from=builder /app/server .
COPY --from=builder /app/web ./web
EXPOSE 80
CMD ["./server"]
