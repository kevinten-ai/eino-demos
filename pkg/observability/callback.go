package observability

import (
	"context"
	"sync"
	"time"

	"github.com/cloudwego/eino/callbacks"
	"github.com/cloudwego/eino/schema"
)

// MetricsStore 指标存储
type MetricsStore struct {
	mu      sync.RWMutex
	calls   []LLMCall
	total   int
	errors  int
}

type LLMCall struct {
	Timestamp  time.Time `json:"timestamp"`
	Model      string    `json:"model"`
	InputLen   int       `json:"input_len"`
	OutputLen  int       `json:"output_len"`
	LatencyMs  int64     `json:"latency_ms"`
	Error      string    `json:"error,omitempty"`
}

func NewMetricsStore() *MetricsStore {
	return &MetricsStore{
		calls: make([]LLMCall, 0),
	}
}

func (s *MetricsStore) Record(call LLMCall) {
	s.mu.Lock()
	defer s.mu.Unlock()
	s.calls = append(s.calls, call)
	s.total++
	if call.Error != "" {
		s.errors++
	}
}

func (s *MetricsStore) GetRecent(limit int) []LLMCall {
	s.mu.RLock()
	defer s.mu.RUnlock()
	if limit > len(s.calls) {
		limit = len(s.calls)
	}
	start := len(s.calls) - limit
	if start < 0 {
		start = 0
	}
	return s.calls[start:]
}

func (s *MetricsStore) Stats() map[string]any {
	s.mu.RLock()
	defer s.mu.RUnlock()
	var totalLatency int64
	var totalInput, totalOutput int
	for _, c := range s.calls {
		totalLatency += c.LatencyMs
		totalInput += c.InputLen
		totalOutput += c.OutputLen
	}
	avgLatency := int64(0)
	if s.total > 0 {
		avgLatency = totalLatency / int64(s.total)
	}
	return map[string]any{
		"total_calls":   s.total,
		"error_calls":   s.errors,
		"avg_latency_ms": avgLatency,
		"total_input_tokens":  totalInput,
		"total_output_tokens": totalOutput,
	}
}

// TracingCallback Eino 回调实现
type TracingCallback struct {
	callbacks.HandlerBuilder
	store   *MetricsStore
	startAt map[string]time.Time
	mu      sync.RWMutex
}

func NewTracingCallback(store *MetricsStore) *TracingCallback {
	return &TracingCallback{
		store:   store,
		startAt: make(map[string]time.Time),
	}
}

func (cb *TracingCallback) OnStart(ctx context.Context, info *callbacks.RunInfo, input callbacks.CallbackInput) context.Context {
	cb.mu.Lock()
	cb.startAt[info.RunID] = time.Now()
	cb.mu.Unlock()
	return ctx
}

func (cb *TracingCallback) OnEnd(ctx context.Context, info *callbacks.RunInfo, output callbacks.CallbackOutput) context.Context {
	cb.record(info, output, nil)
	return ctx
}

func (cb *TracingCallback) OnError(ctx context.Context, info *callbacks.RunInfo, err error) context.Context {
	cb.record(info, nil, err)
	return ctx
}

func (cb *TracingCallback) record(info *callbacks.RunInfo, output callbacks.CallbackOutput, err error) {
	cb.mu.RLock()
	start, ok := cb.startAt[info.RunID]
	cb.mu.RUnlock()
	if !ok {
		return
	}

	call := LLMCall{
		Timestamp: time.Now(),
		LatencyMs: time.Since(start).Milliseconds(),
		Model:     info.Name,
	}

	if input, ok := info.Input.(*schema.Message); ok && input != nil {
		call.InputLen = len(input.Content)
	}
	if out, ok := output.(*schema.Message); ok && out != nil {
		call.OutputLen = len(out.Content)
	}
	if err != nil {
		call.Error = err.Error()
	}

	cb.store.Record(call)
	cb.mu.Lock()
	delete(cb.startAt, info.RunID)
	cb.mu.Unlock()
}
