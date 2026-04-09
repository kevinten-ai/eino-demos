package rag

import (
	"context"
	"fmt"
	"strings"

	"github.com/cloudwego/eino/components/embedding"
	"github.com/cloudwego/eino/components/model"
	"github.com/cloudwego/eino/components/prompt"
	"github.com/cloudwego/eino/compose"
	"github.com/cloudwego/eino/schema"
	"github.com/kevinten-ai/eino-demos/pkg/memory"
)

// Engine RAG 引擎
type Engine struct {
	embedder    embedding.Embedder
	chatModel   model.ToolCallingChatModel
	vectorStore *memory.InMemoryVectorStore
}

func NewEngine(embedder embedding.Embedder, chatModel model.ToolCallingChatModel) *Engine {
	return &Engine{
		embedder:    embedder,
		chatModel:   chatModel,
		vectorStore: memory.NewInMemoryVectorStore(),
	}
}

// AddDocuments 添加文档（自动切分和向量化）
func (e *Engine) AddDocuments(ctx context.Context, documents []string) error {
	chunks := e.chunkDocuments(documents)
	for _, chunk := range chunks {
		vectors, err := e.embedder.EmbedStrings(ctx, []string{chunk})
		if err != nil {
			return fmt.Errorf("embedding failed: %w", err)
		}
		if len(vectors) == 0 {
			continue
		}
		e.vectorStore.Add(memory.VectorRecord{
			ID:      fmt.Sprintf("chunk-%d", len(e.vectorStore.Search(vectors[0], 1))), // 简单 ID
			Content: chunk,
			Vector:  vectors[0],
		})
	}
	return nil
}

// Query 问答
func (e *Engine) Query(ctx context.Context, question string) (string, error) {
	queryVectors, err := e.embedder.EmbedStrings(ctx, []string{question})
	if err != nil {
		return "", fmt.Errorf("embed query failed: %w", err)
	}
	if len(queryVectors) == 0 {
		return "", fmt.Errorf("empty embedding")
	}

	records := e.vectorStore.Search(queryVectors[0], 3)
	var contexts []string
	for _, r := range records {
		contexts = append(contexts, r.Content)
	}
	contextText := strings.Join(contexts, "\n---\n")

	tpl := prompt.FromMessages(schema.FString,
		schema.SystemMessage("你是一个知识库助手。请根据以下参考资料回答问题，如果资料中没有相关信息，请直接说明不知道。"),
		schema.UserMessage("参考资料：\n{context}\n\n问题：{question}"),
	)

	chain := compose.NewChain[map[string]any, *schema.Message]()
	chain.AppendChatTemplate(tpl)
	chain.AppendChatModel(e.chatModel)

	runnable, err := chain.Compile(ctx)
	if err != nil {
		return "", err
	}

	resp, err := runnable.Invoke(ctx, map[string]any{
		"context":  contextText,
		"question": question,
	})
	if err != nil {
		return "", err
	}
	return resp.Content, nil
}

func (e *Engine) chunkDocuments(docs []string) []string {
	var chunks []string
	for _, doc := range docs {
		// 简单按段落切分
		parts := strings.Split(doc, "\n")
		var buf strings.Builder
		for _, p := range parts {
			p = strings.TrimSpace(p)
			if p == "" {
				continue
			}
			if buf.Len() > 500 {
				chunks = append(chunks, buf.String())
				buf.Reset()
			}
			if buf.Len() > 0 {
				buf.WriteByte('\n')
			}
			buf.WriteString(p)
		}
		if buf.Len() > 0 {
			chunks = append(chunks, buf.String())
		}
	}
	return chunks
}
