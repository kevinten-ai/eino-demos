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
	Vector  []float64
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

func (s *InMemoryVectorStore) Search(query []float64, topK int) []VectorRecord {
	s.mu.RLock()
	defer s.mu.RUnlock()
	if topK <= 0 {
		return []VectorRecord{}
	}

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

func cosineSimilarity(a, b []float64) float64 {
	if len(a) != len(b) {
		return 0
	}
	var dot, normA, normB float64
	for i := 0; i < len(a); i++ {
		dot += a[i] * b[i]
		normA += a[i] * a[i]
		normB += b[i] * b[i]
	}
	if normA == 0 || normB == 0 {
		return 0
	}
	return dot / (math.Sqrt(normA) * math.Sqrt(normB))
}
