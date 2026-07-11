package config

import (
	"context"
	"strings"
	"testing"
)

func TestEmbedderWithoutKeyFailsExplicitly(t *testing.T) {
	t.Setenv("DASHSCOPE_API_KEY", "")
	embedder := MustNewEmbedder(context.Background())
	vectors, err := embedder.EmbedStrings(context.Background(), []string{"hello"})
	if err == nil {
		t.Fatal("EmbedStrings without a key returned no error")
	}
	if vectors != nil {
		t.Fatalf("EmbedStrings without a key returned vectors: %v", vectors)
	}
	if !strings.Contains(err.Error(), "DASHSCOPE_API_KEY") {
		t.Fatalf("error %q does not explain the missing key", err)
	}
}
