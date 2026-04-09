package memory

import (
	"context"
	"sync"

	"github.com/cloudwego/eino/schema"
)

// ConversationStore 会话存储接口
type ConversationStore interface {
	AddMessages(sessionID string, msgs []*schema.Message)
	GetMessages(sessionID string) []*schema.Message
	Clear(sessionID string)
}

// InMemoryConversationStore 内存会话存储
type InMemoryConversationStore struct {
	mu       sync.RWMutex
	sessions map[string][]*schema.Message
}

func NewInMemoryConversationStore() ConversationStore {
	return &InMemoryConversationStore{
		sessions: make(map[string][]*schema.Message),
	}
}

func (s *InMemoryConversationStore) AddMessages(sessionID string, msgs []*schema.Message) {
	s.mu.Lock()
	defer s.mu.Unlock()
	s.sessions[sessionID] = append(s.sessions[sessionID], msgs...)
}

func (s *InMemoryConversationStore) GetMessages(sessionID string) []*schema.Message {
	s.mu.RLock()
	defer s.mu.RUnlock()
	// 返回副本避免外部修改
	orig := s.sessions[sessionID]
	if orig == nil {
		return nil
	}
	copyMsgs := make([]*schema.Message, len(orig))
	for i, m := range orig {
		copyMsgs[i] = m
	}
	return copyMsgs
}

func (s *InMemoryConversationStore) Clear(sessionID string) {
	s.mu.Lock()
	defer s.mu.Unlock()
	delete(s.sessions, sessionID)
}
