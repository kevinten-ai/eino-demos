package com.brag.agentscope.examples;

import com.brag.agentscope.config.AgentFactory;
import io.agentscope.core.ReActAgent;
import io.agentscope.core.message.Msg;
import io.agentscope.core.message.MsgRole;
import io.agentscope.core.message.TextBlock;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * MCP客户端管理示例
 * 演示如何管理和监控MCP客户端连接
 */
@Component
@Profile("mcp-client-management-example")
@RequiredArgsConstructor
public class McpClientManagementExample implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(McpClientManagementExample.class);

    private final AgentFactory agentFactory;

    @Override
    public void run(String... args) {
        log.info("=== AgentScope MCP客户端管理示例 ===");

        try {
            // 示例1: 客户端生命周期管理
            demonstrateClientLifecycle();

            // 示例2: 连接监控和健康检查
            demonstrateConnectionMonitoring();

            // 示例3: 负载均衡和故障转移
            demonstrateLoadBalancing();

            log.info("=== MCP客户端管理示例执行完成 ===");

        } catch (Exception e) {
            log.error("MCP客户端管理示例执行失败", e);
        }

        System.exit(0);
    }

    /**
     * 示例1: 客户端生命周期管理
     * 演示MCP客户端的创建、配置、使用和清理
     */
    private void demonstrateClientLifecycle() {
        log.info("--- 客户端生命周期管理示例 ---");

        try {
            // 创建带监控的MCP客户端
            McpClientManager clientManager = new McpClientManager();

            // 注册客户端
            String clientId = clientManager.registerClient("filesystem-client",
                "stdio", "npx", "-y", "@modelcontextprotocol/server-filesystem", "/workspace");

            log.info("已注册MCP客户端: {}", clientId);

            // 测试客户端连接
            boolean isHealthy = clientManager.checkClientHealth(clientId);
            log.info("客户端健康状态: {}", isHealthy ? "正常" : "异常");

            // 使用客户端创建Agent
            ReActAgent fileAgent = agentFactory.createMcpFileAgent(clientId);

            // 执行文件操作任务
            String fileTask = "请列出当前目录下的所有文件";
            log.info("文件任务: {}", fileTask);

            Msg response = callAgent(fileAgent, fileTask);
            log.info("Agent回答: {}", response.getTextContent());

            // 清理客户端
            clientManager.unregisterClient(clientId);
            log.info("已清理MCP客户端: {}", clientId);

        } catch (Exception e) {
            log.warn("客户端生命周期管理示例跳过（需要MCP环境）: {}", e.getMessage());
        }
    }

    /**
     * 示例2: 连接监控和健康检查
     * 演示如何监控MCP客户端的连接状态和性能
     */
    private void demonstrateConnectionMonitoring() {
        log.info("--- 连接监控和健康检查示例 ---");

        try {
            McpMonitoringManager monitoringManager = new McpMonitoringManager();

            // 注册监控客户端
            monitoringManager.registerMonitoredClient("db-client",
                "sse", "https://mcp-db-server.company.com");

            monitoringManager.registerMonitoredClient("api-client",
                "http", "https://mcp-api-server.company.com");

            // 执行监控任务
            log.info("开始监控MCP客户端状态...");

            for (int i = 0; i < 3; i++) {
                monitoringManager.performHealthChecks();

                // 获取监控指标
                var metrics = monitoringManager.getClientMetrics();
                metrics.forEach((clientId, metric) ->
                    log.info("客户端 {} - 响应时间: {}ms, 成功率: {:.2f}%",
                        clientId, metric.getAvgResponseTime(), metric.getSuccessRate() * 100));

                // 等待一段时间
                TimeUnit.SECONDS.sleep(2);
            }

            // 生成监控报告
            String report = monitoringManager.generateHealthReport();
            log.info("健康监控报告:\n{}", report);

        } catch (Exception e) {
            log.warn("连接监控示例跳过（需要MCP环境）: {}", e.getMessage());
        }
    }

    /**
     * 示例3: 负载均衡和故障转移
     * 演示如何在多个MCP服务器间进行负载均衡
     */
    private void demonstrateLoadBalancing() {
        log.info("--- 负载均衡和故障转移示例 ---");

        try {
            McpLoadBalancer loadBalancer = new McpLoadBalancer();

            // 添加多个服务器实例
            loadBalancer.addServer("primary-db", "sse", "https://db-server-1.company.com");
            loadBalancer.addServer("secondary-db", "sse", "https://db-server-2.company.com");
            loadBalancer.addServer("backup-db", "sse", "https://db-server-3.company.com");

            // 模拟负载均衡调用
            for (int i = 0; i < 10; i++) {
                String selectedServer = loadBalancer.selectServer("database-query");
                log.info("请求 {} 路由到服务器: {}", i + 1, selectedServer);

                // 模拟服务器处理
                boolean success = simulateServerProcessing(selectedServer);
                loadBalancer.recordResult(selectedServer, success);

                TimeUnit.MILLISECONDS.sleep(100);
            }

            // 显示负载均衡统计
            var stats = loadBalancer.getLoadStats();
            log.info("负载均衡统计:");
            stats.forEach((server, stat) ->
                log.info("  {}: 请求数={}, 成功率={:.2f}%, 平均响应时间={}ms",
                    server, stat.getRequestCount(),
                    stat.getSuccessRate() * 100, stat.getAvgResponseTime()));

        } catch (Exception e) {
            log.warn("负载均衡示例跳过（需要多服务器环境）: {}", e.getMessage());
        }
    }

    /**
     * 调用Agent的辅助方法
     */
    private Msg callAgent(ReActAgent agent, String message) {
        Msg userMsg = Msg.builder()
                .name("user")
                .role(MsgRole.USER)
                .content(List.of(TextBlock.builder().text(message).build()))
                .build();

        return agent.call(userMsg).block();
    }

    /**
     * 模拟服务器处理的辅助方法
     */
    private boolean simulateServerProcessing(String serverId) {
        // 模拟不同的服务器性能
        switch (serverId) {
            case "primary-db": return Math.random() > 0.1;   // 90% 成功率
            case "secondary-db": return Math.random() > 0.2; // 80% 成功率
            case "backup-db": return Math.random() > 0.3;    // 70% 成功率
            default: return true;
        }
    }

    // 模拟MCP客户端管理器
    static class McpClientManager {
        public String registerClient(String name, String transport, String... command) {
            // 模拟客户端注册
            return name + "-" + System.currentTimeMillis();
        }

        public boolean checkClientHealth(String clientId) {
            // 模拟健康检查
            return true;
        }

        public void unregisterClient(String clientId) {
            // 模拟客户端清理
        }
    }

    // 模拟监控管理器
    static class McpMonitoringManager {
        public void registerMonitoredClient(String name, String transport, String url) {
            // 模拟注册监控客户端
        }

        public void performHealthChecks() {
            // 模拟健康检查
        }

        public java.util.Map<String, ClientMetric> getClientMetrics() {
            // 返回模拟指标
            return java.util.Map.of(
                "db-client", new ClientMetric(150, 0.95),
                "api-client", new ClientMetric(200, 0.98)
            );
        }

        public String generateHealthReport() {
            return "所有MCP客户端运行正常";
        }
    }

    // 模拟负载均衡器
    static class McpLoadBalancer {
        private final java.util.List<String> servers = new java.util.ArrayList<>();
        private final java.util.Map<String, ServerStat> stats = new java.util.HashMap<>();

        public void addServer(String name, String transport, String url) {
            servers.add(name);
            stats.put(name, new ServerStat());
        }

        public String selectServer(String requestType) {
            // 简单的轮询策略
            return servers.get((int) (System.currentTimeMillis() % servers.size()));
        }

        public void recordResult(String server, boolean success) {
            stats.get(server).recordResult(success, 100 + (int)(Math.random() * 200));
        }

        public java.util.Map<String, ServerStat> getLoadStats() {
            return stats;
        }
    }

    // 辅助类
    static class ClientMetric {
        private final long avgResponseTime;
        private final double successRate;

        public ClientMetric(long avgResponseTime, double successRate) {
            this.avgResponseTime = avgResponseTime;
            this.successRate = successRate;
        }

        public long getAvgResponseTime() { return avgResponseTime; }
        public double getSuccessRate() { return successRate; }
    }

    static class ServerStat {
        private int requestCount = 0;
        private int successCount = 0;
        private long totalResponseTime = 0;

        public void recordResult(boolean success, long responseTime) {
            requestCount++;
            if (success) successCount++;
            totalResponseTime += responseTime;
        }

        public int getRequestCount() { return requestCount; }
        public double getSuccessRate() { return requestCount > 0 ? (double) successCount / requestCount : 0; }
        public long getAvgResponseTime() { return requestCount > 0 ? totalResponseTime / requestCount : 0; }
    }
}
