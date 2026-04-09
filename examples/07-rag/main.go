// Demo 07: RAG（检索增强生成）
// 学习目标: 了解 Eino 的 RAG 完整流程：文档切分、Embedding、向量检索、生成回答
//
// 核心概念:
//   - embedding.Embedder: 将文本转换为向量
//   - memory.InMemoryVectorStore: 内存向量存储 + 余弦相似度检索
//   - rag.Engine: 自定义 RAG 引擎，封装检索 + 生成的完整流程
//   - Prompt 注入上下文：将检索到的文档片段作为 context 传给 LLM
package main

import (
	"context"
	"fmt"

	"github.com/kevinten-ai/eino-demos/internal/config"
	"github.com/kevinten-ai/eino-demos/pkg/rag"
)

func main() {
	ctx := context.Background()
	chatModel := config.MustNewChatModel(ctx)
	embedder := config.MustNewEmbedder(ctx)

	engine := rag.NewEngine(embedder, chatModel)

	fmt.Println("=== RAG Demo ===")

	// 模拟知识库文档
	docs := []string{
		"Eino 是字节跳动 CloudWeGo 开源的 Go 语言 AI 应用开发框架。\n" +
			"它提供了组件化、可编排的 LLM 应用开发能力，支持 Chain、Graph、Agent 等多种模式。",
		"Eino 的核心特性包括：\n" +
			"1. 组件抽象（ChatModel、Tool、Prompt、Retriever 等）\n" +
			"2. 编排引擎（Chain 线性链 + Graph DAG 图）\n" +
			"3. Agent 能力（ReAct、ADK）\n" +
			"4. 流式处理和回调系统",
		"RAG（Retrieval-Augmented Generation）是一种结合检索和生成的技术。\n" +
			"先通过 Embedding 将文档向量化存入 Vector Store，用户提问时检索最相关的文档片段，\n" +
			"再将这些片段作为上下文注入 Prompt 中，让 LLM 基于知识库回答。",
	}

	fmt.Println("正在构建知识库向量索引...")
	if err := engine.AddDocuments(ctx, docs); err != nil {
		fmt.Printf("索引失败: %v\n", err)
		return
	}
	fmt.Printf("索引完成\n\n")

	// 提问
	questions := []string{
		"Eino 是什么？",
		"RAG 的原理是什么？",
		"Golang 是谁发明的？", // 知识库外的问题
	}

	for _, q := range questions {
		fmt.Printf("问题: %s\n", q)
		answer, err := engine.Query(ctx, q)
		if err != nil {
			fmt.Printf("回答失败: %v\n", err)
			continue
		}
		fmt.Printf("回答: %s\n\n", answer)
	}
}
