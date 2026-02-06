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
 * 跨会话记忆示例
 * 演示AgentScope中跨会话记忆的管理和应用
 */
@Component
@Profile("cross-session-memory-example")
@RequiredArgsConstructor
public class CrossSessionMemoryExample implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(CrossSessionMemoryExample.class);

    private final AgentFactory agentFactory;

    @Override
    public void run(String... args) {
        log.info("=== AgentScope 跨会话记忆示例 ===");

        try {
            // 示例1: 会话连续性保持
            demonstrateSessionContinuity();

            // 示例2: 记忆关联和检索
            demonstrateMemoryAssociation();

            // 示例3: 长期记忆管理
            demonstrateLongTermMemory();

            log.info("=== 跨会话记忆示例执行完成 ===");

        } catch (Exception e) {
            log.error("跨会话记忆示例执行失败", e);
        }

        System.exit(0);
    }

    /**
     * 示例1: 会话连续性保持
     * 演示在多个会话间保持上下文连续性
     */
    private void demonstrateSessionContinuity() {
        log.info("--- 会话连续性保持示例 ---");

        CrossSessionManager sessionManager = new CrossSessionManager(agentFactory);

        String userId = "user-" + UUID.randomUUID().toString().substring(0, 8);

        // 模拟多轮会话
        List<String> conversations = List.of(
            "我正在学习Java编程",
            "我对Spring框架很感兴趣",
            "能帮我推荐一些Spring的学习资源吗？",
            "我想要深入了解Spring Boot",
            "之前提到的学习路径怎么样？"
        );

        log.info("开始多会话对话流程...");

        for (int i = 0; i < conversations.size(); i++) {
            String conversation = conversations.get(i);
            String sessionId = "session-" + i;

            log.info("会话 {}: {}", i + 1, conversation);

            SessionResult result = sessionManager.processInSession(userId, sessionId, conversation);

            log.info("Agent响应: {}", result.getResponse());
            log.info("记忆上下文: {} 条消息", result.getMemoryContextSize());
        }

        // 验证跨会话记忆
        MemorySummary summary = sessionManager.getUserMemorySummary(userId);
        log.info("用户记忆汇总:");
        log.info("  总会话数: {}", summary.getTotalSessions());
        log.info("  总消息数: {}", summary.getTotalMessages());
        log.info("  活跃主题: {}", String.join(", ", summary.getActiveTopics()));
        log.info("  学习偏好: {}", summary.getLearningPreferences());

        log.info("✅ 会话连续性保持完成");
    }

    /**
     * 示例2: 记忆关联和检索
     * 演示基于内容的记忆关联和智能检索
     */
    private void demonstrateMemoryAssociation() {
        log.info("--- 记忆关联和检索示例 ---");

        MemoryAssociationEngine associationEngine = new MemoryAssociationEngine(agentFactory);

        // 建立用户的记忆网络
        String userId = "user-" + UUID.randomUUID().toString().substring(0, 8);

        // 添加不同主题的记忆
        List<MemoryEntry> memories = List.of(
            new MemoryEntry("编程基础", "用户正在学习Java基础语法", "技术"),
            new MemoryEntry("Spring框架", "用户对Spring IoC和AOP感兴趣", "技术"),
            new MemoryEntry("数据库设计", "用户需要了解MySQL索引优化", "技术"),
            new MemoryEntry("项目管理", "用户在学习敏捷开发方法", "管理"),
            new MemoryEntry("团队协作", "用户关注代码审查流程", "管理")
        );

        // 建立记忆关联
        associationEngine.buildMemoryNetwork(userId, memories);

        // 测试记忆检索
        List<String> queries = List.of(
            "Java编程相关的知识",
            "软件开发管理的内容",
            "数据库优化技巧",
            "团队协作经验"
        );

        for (String query : queries) {
            log.info("查询: {}", query);

            RetrievalResult result = associationEngine.retrieveAssociatedMemories(userId, query);

            log.info("检索结果 ({}/{}):", result.getRetrievedCount(), result.getTotalMatches());
            result.getTopMemories().forEach(memory ->
                log.info("  - {} ({})", memory.getTitle(), memory.getRelevance()));
        }

        // 测试记忆推荐
        String currentTopic = "微服务架构";
        RecommendationResult recommendations = associationEngine.recommendRelatedMemories(userId, currentTopic);

        log.info("相关记忆推荐:");
        recommendations.getRecommendations().forEach(rec ->
            log.info("  {}: {} (理由: {})", rec.getTitle(), rec.getRelevance(), rec.getReason()));

        log.info("✅ 记忆关联和检索完成");
    }

    /**
     * 示例3: 长期记忆管理
     * 演示长期记忆的存储、压缩和清理
     */
    private void demonstrateLongTermMemory() {
        log.info("--- 长期记忆管理示例 ---");

        LongTermMemoryManager memoryManager = new LongTermMemoryManager(agentFactory);

        String userId = "user-" + UUID.randomUUID().toString().substring(0, 8);

        // 模拟长期使用场景
        List<String> timeline = List.of(
            "2024-01-01:开始学习编程",
            "2024-02-01:掌握Java基础",
            "2024-03-01:学习Spring框架",
            "2024-04-01:参与项目开发",
            "2024-05-01:接触微服务架构",
            "2024-06-01:学习DevOps实践",
            "2024-07-01:担任团队负责人",
            "2024-08-01:研究AI应用开发"
        );

        // 建立时间线记忆
        memoryManager.buildTimelineMemory(userId, timeline);

        // 测试记忆压缩
        log.info("执行记忆压缩...");
        CompressionResult compression = memoryManager.compressMemories(userId);

        log.info("压缩结果:");
        log.info("  原始记忆数: {}", compression.getOriginalCount());
        log.info("  压缩后记忆数: {}", compression.getCompressedCount());
        log.info("  压缩率: {:.2f}%", compression.getCompressionRatio() * 100);
        log.info("  保留的关键记忆: {}", compression.getKeyMemoriesRetained());

        // 测试记忆清理
        log.info("执行记忆清理...");
        CleanupResult cleanup = memoryManager.cleanupExpiredMemories(userId);

        log.info("清理结果:");
        log.info("  删除过期记忆: {}", cleanup.getExpiredRemoved());
        log.info("  压缩低价值记忆: {}", cleanup.getLowValueCompressed());
        log.info("  释放存储空间: {} MB", cleanup.getSpaceFreed());

        // 测试记忆回溯
        List<String> historicalQueries = List.of(
            "早期编程学习经历",
            "项目管理经验积累",
            "技术能力发展历程"
        );

        for (String query : historicalQueries) {
            log.info("历史回溯查询: {}", query);

            TimelineResult timelineResult = memoryManager.retrieveTimelineMemories(userId, query);

            log.info("时间线结果:");
            timelineResult.getTimeline().forEach(entry ->
                log.info("  {}: {}", entry.getDate(), entry.getEvent()));
        }

        log.info("✅ 长期记忆管理完成");
    }

    /**
     * 跨会话管理器
     */
    static class CrossSessionManager {

        private final AgentFactory agentFactory;

        public CrossSessionManager(AgentFactory agentFactory) {
            this.agentFactory = agentFactory;
        }

        public SessionResult processInSession(String userId, String sessionId, String message) {
            ReActAgent conversationalAgent = agentFactory.createConversationalAgent();

            // 构建包含历史上下文的提示
            String contextualPrompt = buildContextualPrompt(userId, sessionId, message);

            Msg request = Msg.builder()
                .name("user")
                .role(MsgRole.USER)
                .content(List.of(TextBlock.builder().text(contextualPrompt).build()))
                .build();

            Msg response = conversationalAgent.call(request).block();

            // 记录到跨会话记忆
            recordToCrossSessionMemory(userId, sessionId, message, response.getTextContent());

            return new SessionResult(response.getTextContent(), getMemoryContextSize(userId));
        }

        private String buildContextualPrompt(String userId, String sessionId, String message) {
            // 构建包含历史上下文的提示
            StringBuilder prompt = new StringBuilder();

            prompt.append("用户消息: ").append(message).append("\n\n");

            // 添加用户历史偏好
            prompt.append("用户历史偏好: 学习技术框架，关注最佳实践\n\n");

            // 添加会话连续性提示
            prompt.append("请保持对话的连续性，记住之前讨论的内容。\n");

            return prompt.toString();
        }

        private void recordToCrossSessionMemory(String userId, String sessionId,
                                              String userMessage, String agentResponse) {
            // 模拟记录到跨会话记忆
            log.debug("记录到用户 {} 会话 {} 的记忆", userId, sessionId);
        }

        private int getMemoryContextSize(String userId) {
            // 模拟获取记忆上下文大小
            return 15 + (int)(Math.random() * 10); // 15-25条消息
        }

        public MemorySummary getUserMemorySummary(String userId) {
            // 模拟生成记忆汇总
            return new MemorySummary(
                5,  // 总会话数
                87, // 总消息数
                List.of("Java编程", "Spring框架", "系统设计"), // 活跃主题
                "偏好深入学习和实践应用" // 学习偏好
            );
        }
    }

    /**
     * 记忆关联引擎
     */
    static class MemoryAssociationEngine {

        private final AgentFactory agentFactory;

        public MemoryAssociationEngine(AgentFactory agentFactory) {
            this.agentFactory = agentFactory;
        }

        public void buildMemoryNetwork(String userId, List<MemoryEntry> memories) {
            // 建立记忆间的关联网络
            log.info("为用户 {} 建立记忆网络，包含 {} 条记忆", userId, memories.size());

            // 分析记忆间的关联
            for (MemoryEntry memory : memories) {
                List<String> associations = findAssociations(memory, memories);
                log.info("记忆 '{}' 的关联: {}", memory.getTitle(), associations);
            }
        }

        private List<String> findAssociations(MemoryEntry target, List<MemoryEntry> allMemories) {
            return allMemories.stream()
                .filter(memory -> !memory.equals(target))
                .filter(memory -> hasAssociation(target, memory))
                .map(MemoryEntry::getTitle)
                .collect(java.util.stream.Collectors.toList());
        }

        private boolean hasAssociation(MemoryEntry memory1, MemoryEntry memory2) {
            // 简单的关联检测：相同类别或内容相关
            return memory1.getCategory().equals(memory2.getCategory()) ||
                   memory1.getContent().contains(memory2.getCategory()) ||
                   memory2.getContent().contains(memory1.getCategory());
        }

        public RetrievalResult retrieveAssociatedMemories(String userId, String query) {
            // 模拟基于查询的记忆检索
            List<MemoryWithRelevance> results = List.of(
                new MemoryWithRelevance("Java基础语法", 0.95, "高度相关"),
                new MemoryWithRelevance("Spring IoC原理", 0.87, "相关技术栈"),
                new MemoryWithRelevance("敏捷开发实践", 0.76, "管理相关"),
                new MemoryWithRelevance("数据库设计规范", 0.65, "间接相关"),
                new MemoryWithRelevance("前端框架选择", 0.45, "较低相关")
            );

            List<MemoryWithRelevance> topResults = results.stream()
                .filter(r -> r.getRelevance() > 0.7)
                .collect(java.util.stream.Collectors.toList());

            return new RetrievalResult(topResults.size(), results.size(), topResults);
        }

        public RecommendationResult recommendRelatedMemories(String userId, String currentTopic) {
            // 模拟记忆推荐
            List<MemoryRecommendation> recommendations = List.of(
                new MemoryRecommendation("微服务设计模式", 0.92, "架构设计相关"),
                new MemoryRecommendation("Docker容器化", 0.88, "部署运维相关"),
                new MemoryRecommendation("API设计原则", 0.85, "接口设计相关"),
                new MemoryRecommendation("性能优化技巧", 0.78, "系统优化相关")
            );

            return new RecommendationResult(recommendations);
        }
    }

    /**
     * 长期记忆管理器
     */
    static class LongTermMemoryManager {

        private final AgentFactory agentFactory;

        public LongTermMemoryManager(AgentFactory agentFactory) {
            this.agentFactory = agentFactory;
        }

        public void buildTimelineMemory(String userId, List<String> timeline) {
            log.info("为用户 {} 建立时间线记忆，包含 {} 个时间点", userId, timeline.size());

            // 处理和组织时间线记忆
            timeline.forEach(entry -> {
                String[] parts = entry.split(": ", 2);
                if (parts.length == 2) {
                    log.info("记录时间点: {} - {}", parts[0], parts[1]);
                }
            });
        }

        public CompressionResult compressMemories(String userId) {
            // 模拟记忆压缩
            int originalCount = 150;
            int compressedCount = 45;
            double compressionRatio = 1.0 - (double) compressedCount / originalCount;

            return new CompressionResult(originalCount, compressedCount,
                compressionRatio, 12); // 保留12条关键记忆
        }

        public CleanupResult cleanupExpiredMemories(String userId) {
            // 模拟记忆清理
            return new CleanupResult(23, 8, 156.7); // 删除23条过期，压缩8条，释放156.7MB
        }

        public TimelineResult retrieveTimelineMemories(String userId, String query) {
            // 模拟时间线记忆检索
            List<TimelineEntry> timeline = List.of(
                new TimelineEntry("2024-01", "开始编程学习之旅"),
                new TimelineEntry("2024-03", "深入Spring框架学习"),
                new TimelineEntry("2024-05", "参与微服务项目开发"),
                new TimelineEntry("2024-07", "承担团队技术领导角色"),
                new TimelineEntry("2024-08", "探索AI应用开发")
            );

            return new TimelineResult(timeline);
        }
    }

    // 辅助类
    static class SessionResult {
        private final String response;
        private final int memoryContextSize;

        public SessionResult(String response, int memoryContextSize) {
            this.response = response;
            this.memoryContextSize = memoryContextSize;
        }

        public String getResponse() { return response; }
        public int getMemoryContextSize() { return memoryContextSize; }
    }

    static class MemorySummary {
        private final int totalSessions;
        private final int totalMessages;
        private final List<String> activeTopics;
        private final String learningPreferences;

        public MemorySummary(int totalSessions, int totalMessages,
                           List<String> activeTopics, String learningPreferences) {
            this.totalSessions = totalSessions;
            this.totalMessages = totalMessages;
            this.activeTopics = activeTopics;
            this.learningPreferences = learningPreferences;
        }

        public int getTotalSessions() { return totalSessions; }
        public int getTotalMessages() { return totalMessages; }
        public List<String> getActiveTopics() { return activeTopics; }
        public String getLearningPreferences() { return learningPreferences; }
    }

    static class MemoryEntry {
        private final String title;
        private final String content;
        private final String category;

        public MemoryEntry(String title, String content, String category) {
            this.title = title;
            this.content = content;
            this.category = category;
        }

        public String getTitle() { return title; }
        public String getContent() { return content; }
        public String getCategory() { return category; }
    }

    static class RetrievalResult {
        private final int retrievedCount;
        private final int totalMatches;
        private final List<MemoryWithRelevance> topMemories;

        public RetrievalResult(int retrievedCount, int totalMatches, List<MemoryWithRelevance> topMemories) {
            this.retrievedCount = retrievedCount;
            this.totalMatches = totalMatches;
            this.topMemories = topMemories;
        }

        public int getRetrievedCount() { return retrievedCount; }
        public int getTotalMatches() { return totalMatches; }
        public List<MemoryWithRelevance> getTopMemories() { return topMemories; }
    }

    static class MemoryWithRelevance {
        private final String title;
        private final double relevance;
        private final String reason;

        public MemoryWithRelevance(String title, double relevance, String reason) {
            this.title = title;
            this.relevance = relevance;
            this.reason = reason;
        }

        public String getTitle() { return title; }
        public double getRelevance() { return relevance; }
        public String getReason() { return reason; }
    }

    static class RecommendationResult {
        private final List<MemoryRecommendation> recommendations;

        public RecommendationResult(List<MemoryRecommendation> recommendations) {
            this.recommendations = recommendations;
        }

        public List<MemoryRecommendation> getRecommendations() { return recommendations; }
    }

    static class MemoryRecommendation {
        private final String title;
        private final double relevance;
        private final String reason;

        public MemoryRecommendation(String title, double relevance, String reason) {
            this.title = title;
            this.relevance = relevance;
            this.reason = reason;
        }

        public String getTitle() { return title; }
        public double getRelevance() { return relevance; }
        public String getReason() { return reason; }
    }

    static class CompressionResult {
        private final int originalCount;
        private final int compressedCount;
        private final double compressionRatio;
        private final int keyMemoriesRetained;

        public CompressionResult(int originalCount, int compressedCount,
                               double compressionRatio, int keyMemoriesRetained) {
            this.originalCount = originalCount;
            this.compressedCount = compressedCount;
            this.compressionRatio = compressionRatio;
            this.keyMemoriesRetained = keyMemoriesRetained;
        }

        public int getOriginalCount() { return originalCount; }
        public int getCompressedCount() { return compressedCount; }
        public double getCompressionRatio() { return compressionRatio; }
        public int getKeyMemoriesRetained() { return keyMemoriesRetained; }
    }

    static class CleanupResult {
        private final int expiredRemoved;
        private final int lowValueCompressed;
        private final double spaceFreed;

        public CleanupResult(int expiredRemoved, int lowValueCompressed, double spaceFreed) {
            this.expiredRemoved = expiredRemoved;
            this.lowValueCompressed = lowValueCompressed;
            this.spaceFreed = spaceFreed;
        }

        public int getExpiredRemoved() { return expiredRemoved; }
        public int getLowValueCompressed() { return lowValueCompressed; }
        public double getSpaceFreed() { return spaceFreed; }
    }

    static class TimelineResult {
        private final List<TimelineEntry> timeline;

        public TimelineResult(List<TimelineEntry> timeline) {
            this.timeline = timeline;
        }

        public List<TimelineEntry> getTimeline() { return timeline; }
    }

    static class TimelineEntry {
        private final String date;
        private final String event;

        public TimelineEntry(String date, String event) {
            this.date = date;
            this.event = event;
        }

        public String getDate() { return date; }
        public String getEvent() { return event; }
    }
}
