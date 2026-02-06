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

import java.util.List;
import java.util.UUID;

/**
 * MCP安全示例
 * 演示MCP客户端的安全配置和监控
 */
@Component
@Profile("mcp-security-example")
@RequiredArgsConstructor
public class McpSecurityExample implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(McpSecurityExample.class);

    private final AgentFactory agentFactory;

    @Override
    public void run(String... args) {
        log.info("=== AgentScope MCP安全示例 ===");

        try {
            // 示例1: 安全认证配置
            demonstrateSecureAuthentication();

            // 示例2: 请求拦截和过滤
            demonstrateRequestInterception();

            // 示例3: 审计日志和监控
            demonstrateAuditAndMonitoring();

            log.info("=== MCP安全示例执行完成 ===");

        } catch (Exception e) {
            log.error("MCP安全示例执行失败", e);
        }

        System.exit(0);
    }

    /**
     * 示例1: 安全认证配置
     * 演示如何为MCP客户端配置安全的身份验证
     */
    private void demonstrateSecureAuthentication() {
        log.info("--- 安全认证配置示例 ---");

        try {
            SecureMcpClientFactory clientFactory = new SecureMcpClientFactory();

            // 配置带认证的文件系统客户端
            String fileClientId = clientFactory.createAuthenticatedClient(
                "secure-filesystem",
                "stdio",
                "npx", "-y", "@modelcontextprotocol/server-filesystem", "/secure/data"
            );

            // 配置带认证的数据库客户端
            String dbClientId = clientFactory.createAuthenticatedClient(
                "secure-database",
                "sse",
                "https://secure-db.company.com"
            );

            log.info("已创建安全的MCP客户端: {}, {}", fileClientId, dbClientId);

            // 创建使用安全客户端的Agent
            ReActAgent secureAgent = agentFactory.createSecureMcpAgent(List.of(fileClientId, dbClientId));

            // 执行安全的文件操作
            String secureTask = "请安全地读取配置文件中的数据库连接信息";
            log.info("安全任务: {}", secureTask);

            Msg response = callAgent(secureAgent, secureTask);
            log.info("安全Agent回答: {}", response.getTextContent());

            // 清理安全客户端
            clientFactory.destroyClient(fileClientId);
            clientFactory.destroyClient(dbClientId);

        } catch (Exception e) {
            log.warn("安全认证配置示例跳过（需要安全环境配置）: {}", e.getMessage());
        }
    }

    /**
     * 示例2: 请求拦截和过滤
     * 演示如何拦截和过滤MCP请求以增强安全性
     */
    private void demonstrateRequestInterception() {
        log.info("--- 请求拦截和过滤示例 ---");

        try {
            McpSecurityInterceptor securityInterceptor = new McpSecurityInterceptor();

            // 配置拦截器规则
            securityInterceptor.addRule(new PathTraversalRule());
            securityInterceptor.addRule(new SqlInjectionRule());
            securityInterceptor.addRule(new SensitiveDataRule());

            // 创建带拦截器的MCP客户端
            SecureMcpClient secureClient = new SecureMcpClient(securityInterceptor);

            // 测试拦截器
            List<String> testRequests = List.of(
                "读取文件 /etc/passwd",           // 路径遍历攻击
                "查询用户 WHERE id = 1; DROP TABLE users;", // SQL注入
                "获取密码字段",                   // 敏感数据访问
                "读取文件 /workspace/config.json" // 正常请求
            );

            for (String request : testRequests) {
                boolean allowed = securityInterceptor.isRequestAllowed(request);
                log.info("请求 '{}' - {}", request, allowed ? "允许" : "拦截");

                if (allowed) {
                    // 模拟安全处理
                    String safeResponse = secureClient.processSecureRequest(request);
                    log.info("安全响应: {}", safeResponse);
                }
            }

        } catch (Exception e) {
            log.warn("请求拦截示例跳过: {}", e.getMessage());
        }
    }

    /**
     * 示例3: 审计日志和监控
     * 演示MCP操作的审计日志记录和安全监控
     */
    private void demonstrateAuditAndMonitoring() {
        log.info("--- 审计日志和监控示例 ---");

        try {
            McpAuditLogger auditLogger = new McpAuditLogger();
            McpSecurityMonitor securityMonitor = new McpSecurityMonitor();

            // 启用审计和监控
            auditLogger.startAuditing();
            securityMonitor.startMonitoring();

            // 模拟一系列MCP操作
            List<String> operations = List.of(
                "文件读取操作",
                "数据库查询操作",
                "API调用操作",
                "配置更新操作"
            );

            for (String operation : operations) {
                // 记录审计日志
                auditLogger.logOperation(operation, "user123", System.currentTimeMillis());

                // 执行安全监控
                securityMonitor.monitorOperation(operation);

                // 模拟处理时间
                Thread.sleep(100);
            }

            // 生成安全报告
            String auditReport = auditLogger.generateAuditReport();
            String securityReport = securityMonitor.generateSecurityReport();

            log.info("审计报告:\n{}", auditReport);
            log.info("安全监控报告:\n{}", securityReport);

            // 清理资源
            auditLogger.stopAuditing();
            securityMonitor.stopMonitoring();

        } catch (Exception e) {
            log.warn("审计日志示例跳过: {}", e.getMessage());
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

    // 安全MCP客户端工厂
    static class SecureMcpClientFactory {
        public String createAuthenticatedClient(String name, String transport, String... args) {
            // 模拟创建带认证的客户端
            String clientId = name + "-secure-" + UUID.randomUUID().toString().substring(0, 8);
            log.info("创建安全MCP客户端: {} ({})", clientId, transport);
            return clientId;
        }

        public void destroyClient(String clientId) {
            log.info("销毁安全MCP客户端: {}", clientId);
        }
    }

    // 安全拦截器
    static class McpSecurityInterceptor {
        private final List<SecurityRule> rules = new java.util.ArrayList<>();

        public void addRule(SecurityRule rule) {
            rules.add(rule);
        }

        public boolean isRequestAllowed(String request) {
            return rules.stream().allMatch(rule -> rule.isAllowed(request));
        }
    }

    // 安全规则接口
    interface SecurityRule {
        boolean isAllowed(String request);
    }

    // 路径遍历防护规则
    static class PathTraversalRule implements SecurityRule {
        @Override
        public boolean isAllowed(String request) {
            return !request.contains("../") && !request.contains("..\\") &&
                   !request.contains("/etc/") && !request.contains("\\Windows\\");
        }
    }

    // SQL注入防护规则
    static class SqlInjectionRule implements SecurityRule {
        private static final List<String> SQL_KEYWORDS = List.of(
            "DROP", "DELETE", "UPDATE", "INSERT", "ALTER", "CREATE", "TRUNCATE"
        );

        @Override
        public boolean isAllowed(String request) {
            String upperRequest = request.toUpperCase();
            return SQL_KEYWORDS.stream().noneMatch(upperRequest::contains);
        }
    }

    // 敏感数据访问规则
    static class SensitiveDataRule implements SecurityRule {
        private static final List<String> SENSITIVE_KEYWORDS = List.of(
            "密码", "password", "secret", "token", "key", "credential"
        );

        @Override
        public boolean isAllowed(String request) {
            String lowerRequest = request.toLowerCase();
            return SENSITIVE_KEYWORDS.stream().noneMatch(lowerRequest::contains);
        }
    }

    // 安全MCP客户端
    static class SecureMcpClient {
        private final McpSecurityInterceptor interceptor;

        public SecureMcpClient(McpSecurityInterceptor interceptor) {
            this.interceptor = interceptor;
        }

        public String processSecureRequest(String request) {
            // 模拟安全处理
            return "安全处理完成: " + request;
        }
    }

    // 审计日志记录器
    static class McpAuditLogger {
        private final List<AuditEntry> auditLog = new java.util.ArrayList<>();
        private volatile boolean auditing = false;

        public void startAuditing() {
            auditing = true;
            log.info("MCP审计日志已启用");
        }

        public void stopAuditing() {
            auditing = false;
            log.info("MCP审计日志已停止");
        }

        public void logOperation(String operation, String userId, long timestamp) {
            if (auditing) {
                auditLog.add(new AuditEntry(operation, userId, timestamp));
                log.debug("审计日志: 用户 {} 执行操作 {}", userId, operation);
            }
        }

        public String generateAuditReport() {
            StringBuilder report = new StringBuilder();
            report.append("MCP操作审计报告\n");
            report.append("================\n");
            report.append(String.format("总操作数: %d\n", auditLog.size()));

            // 按用户分组统计
            var userStats = auditLog.stream()
                .collect(java.util.stream.Collectors.groupingBy(AuditEntry::getUserId,
                    java.util.stream.Collectors.counting()));

            report.append("按用户统计:\n");
            userStats.forEach((user, count) ->
                report.append(String.format("  %s: %d 次操作\n", user, count)));

            return report.toString();
        }
    }

    // 安全监控器
    static class McpSecurityMonitor {
        private final java.util.Map<String, Integer> operationCounts = new java.util.HashMap<>();
        private volatile boolean monitoring = false;

        public void startMonitoring() {
            monitoring = true;
            log.info("MCP安全监控已启用");
        }

        public void stopMonitoring() {
            monitoring = false;
            log.info("MCP安全监控已停止");
        }

        public void monitorOperation(String operation) {
            if (monitoring) {
                operationCounts.merge(operation, 1, Integer::sum);
                log.debug("监控到操作: {}", operation);
            }
        }

        public String generateSecurityReport() {
            StringBuilder report = new StringBuilder();
            report.append("MCP安全监控报告\n");
            report.append("==============\n");

            int totalOperations = operationCounts.values().stream().mapToInt(Integer::intValue).sum();
            report.append(String.format("总监控操作数: %d\n", totalOperations));

            report.append("操作类型分布:\n");
            operationCounts.entrySet().stream()
                .sorted(java.util.Map.Entry.<String, Integer>comparingByValue().reversed())
                .forEach(entry ->
                    report.append(String.format("  %s: %d 次\n", entry.getKey(), entry.getValue())));

            return report.toString();
        }
    }

    // 审计条目
    static class AuditEntry {
        private final String operation;
        private final String userId;
        private final long timestamp;

        public AuditEntry(String operation, String userId, long timestamp) {
            this.operation = operation;
            this.userId = userId;
            this.timestamp = timestamp;
        }

        public String getOperation() { return operation; }
        public String getUserId() { return userId; }
        public long getTimestamp() { return timestamp; }
    }
}
