package memory

import (
	"math"
	"sort"
	"sync"
)

// VectorRecord 向量记录
type VectorRecord struct {
	ID      string
	Content string
	Vector  []float32
}

// InMemoryVectorStore 内存向量存储（基于余弦相似度）
type InMemoryVectorStore struct {
	mu      sync.RWMutex
	records []VectorRecord
}

func NewInMemoryVectorStore() *InMemoryVectorStore {
	return &InMemoryVectorStore{
		records: make([]VectorRecord, 0),
	}
}

func (s *InMemoryVectorStore) Add(record VectorRecord) {
	s.mu.Lock()
	defer s.mu.Unlock()
	s.records = append(s.records, record)
}

func (s *InMemoryVectorStore) Search(query []float32, topK int) []VectorRecord {
	s.mu.RLock()
	defer s.mu.RUnlock()

	type scoreRecord struct {
		score float64
		rec   VectorRecord
	}

	results := make([]scoreRecord, 0, len(s.records))
	for _, rec := range s.records {
		sim := cosineSimilarity(query, rec.Vector)
		results = append(results, scoreRecord{score: sim, rec: rec})
	}

	sort.Slice(results, func(i, j int) bool {
		return results[i].score > results[j].score
	})

	if topK > len(results) {
		topK = len(results)
	}

	out := make([]VectorRecord, topK)
	for i := 0; i < topK; i++ {
		out[i] = results[i].rec
	}
	return out
}

func (s *InMemoryVectorStore) Clear() {
	s.mu.Lock()
	defer s.mu.Unlock()
	s.records = s.records[:0]
}

func cosineSimilarity(a, b []float32) float64 {
	if len(a) != len(b) {
		return 0
	}
	var dot, normA, normB float64
	for i := 0; i < len(a); i++ {
		av := float64(a[i])
		bv := float64(b[i])
		dot += av * bv
		normA += av * av
		normB += bv * bv
	}
	if normA == 0 || normB == 0 {
		return 0
	}
	return dot / (math.Sqrt(normA) * math.Sqrt(normB))
}
