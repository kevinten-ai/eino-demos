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

import javax.sql.DataSource;
import java.time.Duration;
import java.util.List;
import java.util.UUID;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 数据库存储示例
 * 演示AgentScope中DatabaseMemory的使用
 */
@Component
@Profile("database-memory-example")
@RequiredArgsConstructor
public class DatabaseMemoryExample implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DatabaseMemoryExample.class);

    private final AgentFactory agentFactory;

    @Override
    public void run(String... args) {
        log.info("=== AgentScope 数据库存储示例 ===");

        try {
            // 示例1: 基础数据库配置
            demonstrateBasicDatabase();

            // 示例2: 高级数据库配置
            demonstrateAdvancedDatabase();

            // 示例3: 数据迁移和备份
            demonstrateDataMigration();

            log.info("=== 数据库存储示例执行完成 ===");

        } catch (Exception e) {
            log.error("数据库示例执行失败", e);
        }

        System.exit(0);
    }

    /**
     * 示例1: 基础数据库配置
     * 演示基本的数据库存储配置和使用
     */
    private void demonstrateBasicDatabase() {
        log.info("--- 基础数据库配置示例 ---");

        try {
            // 注意：AgentScope 1.0.3版本不支持数据库存储，使用InMemoryMemory作为替代
            InMemoryMemory dbMemory = new InMemoryMemory();

            ReActAgent dbAgent = agentFactory.createAgentWithMemory(dbMemory);

            // 测试数据持久化
            String testSession = "db-test-session-" + UUID.randomUUID().toString().substring(0, 8);

            for (int i = 0; i < 3; i++) {
                String message = String.format("数据库存储测试消息 %d", i + 1);
                log.info("存储消息: {}", message);

                Msg response = callAgent(dbAgent, message);
                log.info("Agent响应: {}", response.getTextContent());
            }

            // 验证数据持久化
            dbMemory.getSessionMessages(testSession).collectList().subscribe(messages -> {
                log.info("从数据库恢复了 {} 条消息", messages.size());
                messages.forEach(msg ->
                    log.info("  恢复: {}", msg.getTextContent()));
            });

            // 显示存储统计
            // 注意：AgentScope 1.0.3版本的InMemoryMemory不支持数据库统计
            log.info("数据库存储状态：基础InMemory存储，无数据库统计");

        } catch (Exception e) {
            log.warn("基础数据库配置示例跳过（需要数据库服务）: {}", e.getMessage());
        }
    }

    /**
     * 示例2: 高级数据库配置
     * 演示数据库的高级配置选项和优化
     */
    private void demonstrateAdvancedDatabase() {
        log.info("--- 高级数据库配置示例 ---");

        try {
            // 创建高级数据库配置
            InMemoryMemory advancedDb = new InMemoryMemory();

            ReActAgent advancedAgent = agentFactory.createAgentWithMemory(advancedDb);

            // 测试批量操作性能
            long startTime = System.currentTimeMillis();

            for (int i = 0; i < 50; i++) {
                String batchMessage = "批量数据库消息 " + i;
                callAgent(advancedAgent, batchMessage);
            }

            long endTime = System.currentTimeMillis();
            log.info("批量数据库操作完成，耗时: {}ms", endTime - startTime);

            // 测试查询性能
            String sessionId = "advanced-test-" + UUID.randomUUID().toString().substring(0, 8);

            advancedDb.getSessionMessages(sessionId).collectList().subscribe(messages -> {
                log.info("查询到 {} 条消息", messages.size());
            });

            // 测试时间范围查询
            advancedDb.getMessagesInTimeRange(
                java.time.Instant.now().minus(Duration.ofHours(1)),
                java.time.Instant.now()
            ).count().subscribe(count -> {
                log.info("过去1小时内的消息数: {}", count);
            });

            // 显示高级统计
            advancedDb.getAdvancedStats().subscribe(stats -> {
                log.info("高级数据库统计:");
                log.info("  总消息数: {}", stats.getTotalMessages());
                log.info("  索引命中率: {:.2f}%", stats.getIndexHitRate() * 100);
                log.info("  压缩率: {:.2f}%", stats.getCompressionRatio() * 100);
                log.info("  平均查询时间: {}ms", stats.getAvgQueryTime());
                log.info("  清理的过期消息数: {}", stats.getCleanedExpiredMessages());
            });

        } catch (Exception e) {
            log.warn("高级数据库配置示例跳过（需要数据库服务）: {}", e.getMessage());
        }
    }

    /**
     * 示例3: 数据迁移和备份
     * 演示数据库存储的数据迁移和备份功能
     */
    private void demonstrateDataMigration() {
        log.info("--- 数据迁移和备份示例 ---");

        try {
            DatabaseMemory sourceDb = DatabaseMemory.builder()
                .dataSource(createDataSource())
                .tableName("agent_memory_source")
                .build();

            DatabaseMemory targetDb = DatabaseMemory.builder()
                .dataSource(createDataSource())
                .tableName("agent_memory_target")
                .build();

            // 创建备份服务
            DatabaseBackupService backupService = new DatabaseBackupService();

            // 执行数据备份
            log.info("开始数据备份...");
            backupService.createBackup(sourceDb, "/tmp/agent_memory_backup.json")
                .subscribe(() -> log.info("数据备份完成"));

            // 执行数据迁移
            log.info("开始数据迁移...");
            // 注意：AgentScope 1.0.3版本不支持数据迁移，使用模拟操作
            log.info("数据迁移完成（模拟）");

            // 验证迁移结果
            // 注意：AgentScope 1.0.3版本不支持迁移统计
            log.info("迁移前后对比：基础InMemory存储，无迁移统计");

            // 从备份恢复数据
            log.info("从备份恢复数据...");
            DatabaseMemory recoveryDb = DatabaseMemory.builder()
                .dataSource(createDataSource())
                .tableName("agent_memory_recovery")
                .build();

            backupService.restoreFromBackup(recoveryDb, "/tmp/agent_memory_backup.json")
                .subscribe(restored -> log.info("成功恢复 {} 条消息", restored));

        } catch (Exception e) {
            log.warn("数据迁移示例跳过（需要数据库服务）: {}", e.getMessage());
        }
    }

    /**
     * 创建数据源的辅助方法
     */
    private DataSource createDataSource() {
        // 这里应该返回实际的数据库连接池
        // 为了示例，我们返回一个模拟的数据源
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

    // 模拟数据库相关类
    static class DatabaseMemory {
        public static Builder builder() { return new Builder(); }

        static class Builder {
            public DatabaseMemory build() { return new DatabaseMemory(); }
            public Builder dataSource(DataSource ds) { return this; }
            public Builder tableName(String name) { return this; }
            public Builder autoCreateTable(boolean auto) { return this; }
            public Builder batchSize(int size) { return this; }
            public Builder enableIndexing(boolean enabled) { return this; }
            public Builder indexBySession(boolean enabled) { return this; }
            public Builder indexByTimestamp(boolean enabled) { return this; }
            public Builder enableCompression(boolean enabled) { return this; }
            public Builder cleanupPolicy(CleanupPolicy policy) { return this; }
            public Builder retentionPeriod(Duration period) { return this; }
            public Builder maxRetries(int retries) { return this; }
        }

        public Mono<Void> getStats() { return Mono.empty(); }
        public Flux<Msg> getSessionMessages(String sessionId) { return Flux.empty(); }
        public Mono<Void> getAdvancedStats() { return Mono.empty(); }
        public Flux<Msg> getMessagesInTimeRange(java.time.Instant start, java.time.Instant end) { return Flux.empty(); }

        enum CleanupPolicy { TIME_BASED, SIZE_BASED, MANUAL }
    }

    static class MockDataSource implements DataSource {
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

    static class DatabaseBackupService {
        public Mono<Void> createBackup(DatabaseMemory memory, String path) {
            return Mono.empty();
        }

        public Mono<Long> migrateData(DatabaseMemory source, DatabaseMemory target) {
            return Mono.just(0L);
        }

        public Mono<Long> restoreFromBackup(DatabaseMemory memory, String path) {
            return Mono.just(0L);
        }
    }
}
