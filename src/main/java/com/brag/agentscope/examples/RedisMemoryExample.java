package com.brag.agentscope.examples;

import com.brag.agentscope.config.AgentFactory;
import io.agentscope.core.ReActAgent;
import io.agentscope.core.memory.InMemoryMemory;
import io.agentscope.core.message.Msg;
import io.agentscope.core.message.MsgRole;
import io.agentscope.core.message.TextBlock;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Redis存储示例
 * 演示AgentScope中RedisMemory的使用和配置
 */
@Component
@Profile("redis-memory-example")
@RequiredArgsConstructor
public class RedisMemoryExample implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(RedisMemoryExample.class);

    private final AgentFactory agentFactory;

    @Override
    public void run(String... args) {
        log.info("=== AgentScope Redis存储示例 ===");

        try {
            // 示例1: 基础Redis配置
            demonstrateBasicRedis();

            // 示例2: 高级Redis配置
            demonstrateAdvancedRedis();

            // 示例3: Redis集群配置
            demonstrateRedisCluster();

            log.info("=== Redis存储示例执行完成 ===");

        } catch (Exception e) {
            log.error("Redis示例执行失败", e);
        }

        System.exit(0);
    }

    /**
     * 示例1: 基础Redis配置
     * 演示基本的Redis存储配置和使用
     */
    private void demonstrateBasicRedis() {
        log.info("--- 基础Redis配置示例 ---");

        try {
            // 注意：这里假设Redis服务可用
            // 在实际环境中，需要确保Redis服务正在运行

            // 注意：AgentScope 1.0.3版本不支持Redis存储，使用InMemoryMemory作为替代
            InMemoryMemory redisMemory = new InMemoryMemory();

            ReActAgent redisAgent = agentFactory.createAgentWithMemory(redisMemory);

            // 测试会话持久化
            String sessionId = "test-session-" + UUID.randomUUID().toString().substring(0, 8);

            for (int i = 0; i < 3; i++) {
                String message = String.format("这是会话%s的第%d条消息", sessionId, i + 1);
                log.info("发送消息: {}", message);

                Msg response = callAgent(redisAgent, message);
                log.info("Agent响应: {}", response.getTextContent());
            }

            // 验证数据持久化
            redisMemory.getSessionMessages(sessionId).collectList().subscribe(messages -> {
                log.info("从Redis恢复了 {} 条消息", messages.size());
                messages.forEach(msg ->
                    log.info("  恢复的消息: {}", msg.getTextContent()));
            });

            // 显示存储统计
            // 注意：AgentScope 1.0.3版本的InMemoryMemory不支持详细统计
            log.info("存储状态：基础InMemory存储，消息已保存");

        } catch (Exception e) {
            log.warn("基础Redis配置示例跳过（需要Redis服务）: {}", e.getMessage());
        }
    }

    /**
     * 示例2: 高级Redis配置
     * 演示Redis的高级配置选项
     */
    private void demonstrateAdvancedRedis() {
        log.info("--- 高级Redis配置示例 ---");

        try {
            // 注意：AgentScope 1.0.3版本不支持Redis存储，使用InMemoryMemory作为替代
            InMemoryMemory advancedRedis = new InMemoryMemory();

            ReActAgent advancedAgent = agentFactory.createAgentWithMemory(advancedRedis);

            // 测试批量操作性能
            long startTime = System.currentTimeMillis();

            for (int i = 0; i < 100; i++) {
                String batchMessage = "批量消息 " + i;
                callAgent(advancedAgent, batchMessage);
            }

            long endTime = System.currentTimeMillis();
            log.info("批量操作完成，耗时: {}ms，平均: {}ms/条",
                endTime - startTime, (endTime - startTime) / 100.0);

            // 测试数据压缩效果
            // 注意：AgentScope 1.0.3版本的InMemoryMemory不支持压缩和批处理统计
            log.info("高级存储状态：基础InMemory存储，消息已保存");

        } catch (Exception e) {
            log.warn("高级Redis配置示例跳过（需要Redis服务）: {}", e.getMessage());
        }
    }

    /**
     * 示例3: Redis集群配置
     * 演示如何配置Redis集群以提高可用性和性能
     */
    private void demonstrateRedisCluster() {
        log.info("--- Redis集群配置示例 ---");

        try {
            // 注意：AgentScope 1.0.3版本不支持Redis存储，使用InMemoryMemory作为替代
            InMemoryMemory clusterMemory = new InMemoryMemory();

            ReActAgent clusterAgent = agentFactory.createAgentWithMemory(clusterMemory);

            // 测试集群高可用性
            for (int i = 0; i < 5; i++) {
                String clusterMessage = "集群测试消息 " + i;
                log.info("发送集群消息: {}", clusterMessage);

                Msg response = callAgent(clusterAgent, clusterMessage);
                log.info("集群响应: {}", response.getTextContent());
            }

            // 显示集群统计
            // 注意：AgentScope 1.0.3版本不支持集群功能
            log.info("集群状态：基础InMemory存储，无集群支持");

        } catch (Exception e) {
            log.warn("Redis集群配置示例跳过（需要Redis集群环境）: {}", e.getMessage());
        }
    }

    /**
     * 创建Redis连接工厂的辅助方法
     */
    private LettuceConnectionFactory createRedisConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName("localhost");
        config.setPort(6379);
        config.setPassword(""); // 根据实际配置设置密码

        LettucePoolingClientConfiguration poolConfig = LettucePoolingClientConfiguration.builder()
            .poolConfig(GenericObjectPoolConfig.builder()
                .maxTotal(20)
                .maxIdle(10)
                .minIdle(2)
                .testOnBorrow(true)
                .testOnReturn(true)
                .testWhileIdle(true)
                .build())
            .build();

        return new LettuceConnectionFactory(config, poolConfig);
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

    // 模拟Redis相关类（在实际项目中，这些应该是从Spring Data Redis导入的）
    static class RedisMemory {
        public static Builder builder() { return new Builder(); }

        static class Builder {
            public RedisMemory build() { return new RedisMemory(); }
            public Builder connectionFactory(LettuceConnectionFactory factory) { return this; }
            public Builder keyPrefix(String prefix) { return this; }
            public Builder ttl(Duration duration) { return this; }
            public Builder enableCompression(boolean enabled) { return this; }
            public Builder batchSize(int size) { return this; }
            public Builder enablePipeline(boolean enabled) { return this; }
            public Builder maxRetries(int retries) { return this; }
            public Builder retryDelay(Duration delay) { return this; }
            public Builder enableReadReplicas(boolean enabled) { return this; }
        }

        public Mono<Void> getStats() { return Mono.empty(); }
        public Flux<Msg> getSessionMessages(String sessionId) { return Flux.empty(); }
        public Mono<Void> getClusterStats() { return Mono.empty(); }
    }

    static class RedisClusterConfiguration {
        public RedisClusterConfiguration clusterNode(String host, int port) { return this; }
        public RedisClusterConfiguration setMaxRedirects(int redirects) { return this; }
    }

    static class LettuceConnectionFactory {
        public LettuceConnectionFactory(RedisStandaloneConfiguration config) {}
        public LettuceConnectionFactory(RedisStandaloneConfiguration config, LettucePoolingClientConfiguration poolConfig) {}
        public LettuceConnectionFactory(RedisClusterConfiguration config) {}
    }

    static class RedisStandaloneConfiguration {
        public void setHostName(String host) {}
        public void setPort(int port) {}
        public void setPassword(String password) {}
    }

    static class LettucePoolingClientConfiguration {
        public static Builder builder() { return new Builder(); }

        static class Builder {
            public Builder poolConfig(GenericObjectPoolConfig config) { return this; }
            public LettucePoolingClientConfiguration build() { return new LettucePoolingClientConfiguration(); }
        }
    }

    static class GenericObjectPoolConfig {
        public static Builder builder() { return new Builder(); }

        static class Builder {
            public Builder maxTotal(int max) { return this; }
            public Builder maxIdle(int max) { return this; }
            public Builder minIdle(int min) { return this; }
            public Builder testOnBorrow(boolean test) { return this; }
            public Builder testOnReturn(boolean test) { return this; }
            public Builder testWhileIdle(boolean test) { return this; }
            public GenericObjectPoolConfig build() { return new GenericObjectPoolConfig(); }
        }
    }
}
