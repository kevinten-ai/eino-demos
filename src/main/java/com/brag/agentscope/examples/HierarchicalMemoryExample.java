package com.brag.agentscope.examples;

import com.brag.agentscope.config.AgentFactory;
import io.agentscope.core.ReActAgent;
import io.agentscope.core.memory.InMemoryMemory;
import io.agentscope.core.message.Msg;
import io.agentscope.core.message.MsgRole;
import io.agentscope.core.message.TextBlock;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 分层存储示例
 * 演示AgentScope中分层Memory架构的使用
 */
@Component
@Profile("hierarchical-memory-example")
@RequiredArgsConstructor
public class HierarchicalMemoryExample implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(HierarchicalMemoryExample.class);

    private final AgentFactory agentFactory;

    @Override
    public void run(String... args) {
        log.info("=== AgentScope 分层存储示例 ===");

        try {
            // 示例1: 基础分层存储
            demonstrateBasicHierarchical();

            // 示例2: 自适应分层存储
            demonstrateAdaptiveHierarchical();

            // 示例3: 性能监控和优化
            demonstratePerformanceMonitoring();

            log.info("=== 分层存储示例执行完成 ===");

        } catch (Exception e) {
            log.error("分层存储示例执行失败", e);
        }

        System.exit(0);
    }

    /**
     * 示例1: 基础分层存储
     * 演示三层存储架构：内存 -> Redis -> 数据库
     */
    private void demonstrateBasicHierarchical() {
        log.info("--- 基础分层存储示例 ---");

        try {
            // 创建三层存储架构
            InMemoryMemory l1Cache = InMemoryMemory.builder()
                .maxMessages(1000)
                .compressionEnabled(true)
                .build();

            RedisMemory l2Cache = RedisMemory.builder()
                .connectionFactory(createRedisConnectionFactory())
                .keyPrefix("agentscope:l2:")
                .ttl(Duration.ofHours(24))
                .build();

            DatabaseMemory persistent = DatabaseMemory.builder()
                .dataSource(createDataSource())
                .tableName("agent_memory_hierarchical")
                .build();

            // 注意：AgentScope 1.0.3版本不支持分层存储，使用InMemoryMemory作为替代
            InMemoryMemory hierarchicalMemory = new InMemoryMemory();

            ReActAgent hierarchicalAgent = agentFactory.createAgentWithMemory(hierarchicalMemory);

            // 测试分层存储性能
            long startTime = System.currentTimeMillis();

            for (int i = 0; i < 10; i++) {
                String testMessage = "分层存储测试消息 " + i;
                log.info("发送消息: {}", testMessage);

                Msg response = callAgent(hierarchicalAgent, testMessage);
                log.info("Agent响应: {}", response.getTextContent());
            }

            long endTime = System.currentTimeMillis();
            log.info("分层存储测试完成，耗时: {}ms", endTime - startTime);

            // 显示分层统计
            // 注意：AgentScope 1.0.3版本的InMemoryMemory不支持详细性能统计
            log.info("分层存储统计：基础InMemory存储，无性能统计");

        } catch (Exception e) {
            log.warn("基础分层存储示例跳过（需要完整存储环境）: {}", e.getMessage());
        }
    }

    /**
     * 示例2: 自适应分层存储
     * 演示基于访问模式的自适应存储策略
     */
    private void demonstrateAdaptiveHierarchical() {
        log.info("--- 自适应分层存储示例 ---");

        try {
            // 注意：AgentScope 1.0.3版本不支持自适应存储，使用InMemoryMemory作为替代
            InMemoryMemory adaptiveMemory = new InMemoryMemory();

            ReActAgent adaptiveAgent = agentFactory.createAgentWithMemory(adaptiveMemory);

            // 模拟不同的访问模式
            String[] frequentMessages = {"你好", "谢谢", "再见"};  // 频繁访问
            String[] occasionalMessages = {"今天天气怎么样", "帮我查一下数据"}; // 偶尔访问
            String[] rareMessages = {"分析一下市场趋势", "生成详细报告"}; // 很少访问

            // 测试频繁访问的消息（应该留在L1缓存）
            log.info("测试频繁访问消息:");
            for (int i = 0; i < 5; i++) {
                for (String msg : frequentMessages) {
                    callAgent(adaptiveAgent, msg);
                }
            }

            // 测试偶尔访问的消息（应该在L2缓存）
            log.info("测试偶尔访问消息:");
            for (String msg : occasionalMessages) {
                callAgent(adaptiveAgent, msg);
            }

            // 测试很少访问的消息（应该在数据库）
            log.info("测试很少访问消息:");
            for (String msg : rareMessages) {
                callAgent(adaptiveAgent, msg);
            }

            // 显示自适应统计
            adaptiveMemory.getAdaptiveStats().subscribe(stats -> {
                log.info("自适应存储统计:");
                log.info("  L1缓存中的高频消息: {}", stats.getL1FrequentMessages());
                log.info("  L2缓存中的中频消息: {}", stats.getL2OccasionalMessages());
                log.info("  数据库中的冷数据: {}", stats.getDbColdData());
                log.info("  自动迁移次数: {}", stats.getAutoMigrations());
                log.info("  策略调整次数: {}", stats.getStrategyAdjustments());
            });

        } catch (Exception e) {
            log.warn("自适应分层存储示例跳过（需要完整存储环境）: {}", e.getMessage());
        }
    }

    /**
     * 示例3: 性能监控和优化
     * 演示分层存储的性能监控和自动优化
     */
    private void demonstratePerformanceMonitoring() {
        log.info("--- 性能监控和优化示例 ---");

        try {
            // 注意：AgentScope 1.0.3版本不支持监控存储，使用InMemoryMemory作为替代
            InMemoryMemory monitoredMemory = new InMemoryMemory();

            ReActAgent monitoredAgent = agentFactory.createAgentWithMemory(monitoredMemory);

            // 启动性能监控
            monitoredMemory.startMonitoring();

            // 执行负载测试
            log.info("开始负载测试...");
            for (int i = 0; i < 100; i++) {
                String loadMessage = "负载测试消息 " + i;
                callAgent(monitoredAgent, loadMessage);

                // 每10次检查一次性能
                if ((i + 1) % 10 == 0) {
                    monitoredMemory.checkPerformance();
                }
            }

            // 显示性能报告
            String performanceReport = monitoredMemory.generatePerformanceReport();
            log.info("性能监控报告:\n{}", performanceReport);

            // 执行自动优化
            log.info("执行自动优化...");
            monitoredMemory.optimize().subscribe(optimized -> {
                if (optimized) {
                    log.info("✅ 存储优化完成");
                } else {
                    log.info("⚠️ 无需优化或优化失败");
                }
            });

            // 停止监控
            monitoredMemory.stopMonitoring();

        } catch (Exception e) {
            log.warn("性能监控示例跳过（需要完整存储环境）: {}", e.getMessage());
        }
    }

    /**
     * 创建Redis连接工厂的辅助方法
     */
    private org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory createRedisConnectionFactory() {
        // 使用Spring Data Redis的LettuceConnectionFactory
        // 注意：这只是示例，实际使用时需要正确配置Redis连接
        return null; // 实际项目中应该返回有效的连接工厂实例
    }

    /**
     * 创建数据源的辅助方法
     */
    private javax.sql.DataSource createDataSource() {
        // 模拟数据源
        return new MockDataSource();
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

    // 模拟分层存储类
    static class HierarchicalMemory {
        public HierarchicalMemory(InMemoryMemory l1, RedisMemory l2, DatabaseMemory db) {}

        public Mono<Void> getStats() { return Mono.empty(); }
    }

    static class AdaptiveMemory {
        public AdaptiveMemory(InMemoryMemory l1, RedisMemory l2, DatabaseMemory db) {}

        public Mono<Void> getAdaptiveStats() { return Mono.empty(); }
    }

    static class MonitoredHierarchicalMemory {
        public MonitoredHierarchicalMemory(InMemoryMemory l1, RedisMemory l2, DatabaseMemory db) {}

        public void startMonitoring() {}
        public void checkPerformance() {}
        public String generatePerformanceReport() { return "性能报告"; }
        public Mono<Boolean> optimize() { return Mono.just(true); }
        public void stopMonitoring() {}
    }

    // 模拟存储类
    static class InMemoryMemory {
        public static Builder builder() { return new Builder(); }

        static class Builder {
            public InMemoryMemory build() { return new InMemoryMemory(); }
            public Builder maxMessages(int max) { return this; }
            public Builder compressionEnabled(boolean enabled) { return this; }
        }
    }

    static class RedisMemory {
        public static Builder builder() { return new Builder(); }

        static class Builder {
            public RedisMemory build() { return new RedisMemory(); }
            public Builder connectionFactory(org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory factory) { return this; }
            public Builder keyPrefix(String prefix) { return this; }
            public Builder ttl(Duration duration) { return this; }
        }
    }

    static class DatabaseMemory {
        public static Builder builder() { return new Builder(); }

        static class Builder {
            public DatabaseMemory build() { return new DatabaseMemory(); }
            public Builder dataSource(javax.sql.DataSource ds) { return this; }
            public Builder tableName(String name) { return this; }
        }
    }

    static class MockLettuceConnectionFactory {}

    static class MockDataSource implements javax.sql.DataSource {
        @Override public java.sql.Connection getConnection() { return null; }
        @Override public java.sql.Connection getConnection(String username, String password) { return null; }
        @Override public java.io.PrintWriter getLogWriter() { return null; }
        @Override public void setLogWriter(java.io.PrintWriter out) {}
        @Override public void setLoginTimeout(int seconds) {}
        @Override public int getLoginTimeout() { return 0; }
        @Override public java.util.logging.Logger getParentLogger() { return null; }
        @Override public <T> T unwrap(Class<T> iface) { return null; }
        @Override public boolean isWrapperFor(Class<?> iface) { return false; }
    }
}
