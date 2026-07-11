package memory

import (
	"testing"

	"github.com/cloudwego/eino/schema"
)

func TestConversationStoreDoesNotExposeMessagePointers(t *testing.T) {
	store := NewInMemoryConversationStore()
	original := schema.UserMessage("original")
	store.AddMessages("session", []*schema.Message{original})

	original.Content = "mutated after add"
	firstRead := store.GetMessages("session")
	if firstRead[0].Content != "original" {
		t.Fatalf("stored message changed to %q", firstRead[0].Content)
	}

	firstRead[0].Content = "mutated after read"
	secondRead := store.GetMessages("session")
	if secondRead[0].Content != "original" {
		t.Fatalf("GetMessages exposed stored message: %q", secondRead[0].Content)
	}
}

func TestVectorStoreHandlesBoundsAndRanksResults(t *testing.T) {
	store := NewInMemoryVectorStore()
	store.Add(VectorRecord{ID: "close", Vector: []float64{1, 0}})
	store.Add(VectorRecord{ID: "far", Vector: []float64{0, 1}})

	if got := store.Search([]float64{1, 0}, 0); len(got) != 0 {
		t.Fatalf("Search with topK=0 returned %d records", len(got))
	}
	got := store.Search([]float64{1, 0}, 10)
	if len(got) != 2 || got[0].ID != "close" {
		t.Fatalf("unexpected ranking: %#v", got)
	}
}
