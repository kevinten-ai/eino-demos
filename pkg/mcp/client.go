package mcp

import (
	"bytes"
	"context"
	"encoding/json"
	"fmt"
	"io"
	"net/http"
	"sync"
	"time"
)

// Tool MCP 工具定义
type Tool struct {
	Name        string          `json:"name"`
	Description string          `json:"description"`
	InputSchema json.RawMessage `json:"inputSchema"`
}

// Client MCP 客户端
type Client struct {
	baseURL    string
	httpClient *http.Client
	mu         sync.RWMutex
	tools      []Tool
}

func NewClient(baseURL string) *Client {
	return &Client{
		baseURL:    baseURL,
		httpClient: &http.Client{Timeout: 30 * time.Second},
		tools:      make([]Tool, 0),
	}
}

// DiscoverTools 从 MCP Server 发现工具
func (c *Client) DiscoverTools(ctx context.Context) ([]Tool, error) {
	req, err := http.NewRequestWithContext(ctx, "GET", c.baseURL+"/tools", nil)
	if err != nil {
		return nil, err
	}
	resp, err := c.httpClient.Do(req)
	if err != nil {
		return nil, err
	}
	defer resp.Body.Close()

	if resp.StatusCode != http.StatusOK {
		body, _ := io.ReadAll(resp.Body)
		return nil, fmt.Errorf("discover tools failed: %s", string(body))
	}

	var tools []Tool
	if err := json.NewDecoder(resp.Body).Decode(&tools); err != nil {
		return nil, err
	}

	c.mu.Lock()
	c.tools = tools
	c.mu.Unlock()
	return tools, nil
}

// CallTool 调用 MCP 工具
func (c *Client) CallTool(ctx context.Context, name string, arguments map[string]any) (string, error) {
	payload := map[string]any{
		"name":      name,
		"arguments": arguments,
	}
	data, err := json.Marshal(payload)
	if err != nil {
		return "", err
	}

	req, err := http.NewRequestWithContext(ctx, "POST", c.baseURL+"/tools/call", bytes.NewReader(data))
	if err != nil {
		return "", err
	}
	req.Header.Set("Content-Type", "application/json")

	resp, err := c.httpClient.Do(req)
	if err != nil {
		return "", err
	}
	defer resp.Body.Close()

	body, err := io.ReadAll(resp.Body)
	if err != nil {
		return "", err
	}
	if resp.StatusCode != http.StatusOK {
		return "", fmt.Errorf("tool call failed: %s", string(body))
	}
	return string(body), nil
}

func (c *Client) GetTools() []Tool {
	c.mu.RLock()
	defer c.mu.RUnlock()
	out := make([]Tool, len(c.tools))
	copy(out, c.tools)
	return out
}
