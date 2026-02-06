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
import java.util.Map;

/**
 * 语义会话检索示例
 * 演示AgentScope中基于语义的会话检索和上下文感知
 */
@Component
@Profile("semantic-session-retrieval-example")
@RequiredArgsConstructor
public class SemanticSessionRetrievalExample implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(SemanticSessionRetrievalExample.class);

    private final AgentFactory agentFactory;

    @Override
    public void run(String... args) {
        log.info("=== AgentScope 语义会话检索示例 ===");

        try {
            // 示例1: 语义相似度检索
            demonstrateSemanticRetrieval();

            // 示例2: 上下文感知推荐
            demonstrateContextAwareRecommendation();

            // 示例3: 多模态会话关联
            demonstrateMultiModalAssociation();

            log.info("=== 语义会话检索示例执行完成 ===");

        } catch (Exception e) {
            log.error("语义会话检索示例执行失败", e);
        }

        System.exit(0);
    }

    /**
     * 示例1: 语义相似度检索
     * 演示基于语义理解的会话内容检索
     */
    private void demonstrateSemanticRetrieval() {
        log.info("--- 语义相似度检索示例 ---");

        SemanticRetriever semanticRetriever = new SemanticRetriever(agentFactory);

        // 建立会话索引
        Map<String, String> sessions = Map.of(
            "session-001", "讨论Java异常处理的最佳实践",
            "session-002", "学习Spring Boot自动配置原理",
            "session-003", "数据库索引优化策略探讨",
            "session-004", "微服务架构设计模式",
            "session-005", "敏捷开发流程和工具使用",
            "session-006", "Docker容器化部署实践"
        );

        semanticRetriever.buildSessionIndex(sessions);

        // 执行语义检索
        List<String> queries = List.of(
            "Java错误处理方法",
            "Spring框架配置问题",
            "数据库性能优化",
            "分布式系统设计",
            "软件开发流程改进",
            "容器化技术应用"
        );

        for (String query : queries) {
            log.info("查询: {}", query);

            RetrievalResult result = semanticRetriever.semanticSearch(query);

            log.info("检索结果 (Top 3):");
            result.getTopResults().forEach((sessionId, score) -> {
                String content = sessions.get(sessionId);
                log.info("  {}: {:.3f} - {}", sessionId, score, content.substring(0, 30) + "...");
            });
        }

        log.info("✅ 语义相似度检索完成");
    }

    /**
     * 示例2: 上下文感知推荐
     * 演示基于用户上下文的智能会话推荐
     */
    private void demonstrateContextAwareRecommendation() {
        log.info("--- 上下文感知推荐示例 ---");

        ContextAwareRecommender recommender = new ContextAwareRecommender(agentFactory);

        // 建立用户画像和历史会话
        UserProfile userProfile = new UserProfile(
            "高级Java开发者",
            List.of("Java", "Spring", "微服务", "DevOps"),
            "架构设计和性能优化",
            5 // 经验年限
        );

        Map<String, SessionInfo> sessionHistory = Map.of(
            "arch-001", new SessionInfo("微服务架构设计", 0.9, "技术讨论"),
            "perf-002", new SessionInfo("JVM性能调优", 0.8, "技术讨论"),
            "agile-003", new SessionInfo("Scrum实践指南", 0.6, "管理讨论"),
            "cloud-004", new SessionInfo("AWS云服务最佳实践", 0.7, "技术讨论"),
            "sec-005", new SessionInfo("应用安全加固", 0.5, "技术讨论")
        );

        recommender.buildUserProfile(userProfile, sessionHistory);

        // 基于不同上下文进行推荐
        List<String> contexts = List.of(
            "正在设计新的微服务系统",
            "需要优化现有系统的性能",
            "考虑采用敏捷开发方法",
            "准备迁移到云平台",
            "加强应用的安全性"
        );

        for (String context : contexts) {
            log.info("当前上下文: {}", context);

            RecommendationResult recommendations = recommender.recommendSessions(context);

            log.info("推荐结果:");
            recommendations.getRecommendations().forEach(rec ->
                log.info("  {} (相关度: {:.2f}, 理由: {})",
                    rec.getSessionTitle(), rec.getRelevanceScore(), rec.getReason()));
        }

        log.info("✅ 上下文感知推荐完成");
    }

    /**
     * 示例3: 多模态会话关联
     * 演示结合文本、代码、图表等多种模态的会话关联
     */
    private void demonstrateMultiModalAssociation() {
        log.info("--- 多模态会话关联示例 ---");

        MultiModalAssociator associator = new MultiModalAssociator(agentFactory);

        // 定义多模态会话内容
        Map<String, MultiModalContent> multiModalSessions = Map.of(
            "code-review-001", new MultiModalContent(
                "代码审查实践",
                "代码示例和审查清单",
                "类图和流程图",
                List.of("代码质量", "最佳实践", "团队协作")
            ),
            "design-doc-002", new MultiModalContent(
                "系统设计文档",
                "架构图和数据库模型",
                "API接口定义",
                List.of("系统设计", "API设计", "文档编写")
            ),
            "perf-analysis-003", new MultiModalContent(
                "性能分析报告",
                "性能测试数据和图表",
                "优化建议和代码示例",
                List.of("性能优化", "监控分析", "数据可视化")
            )
        );

        associator.indexMultiModalContent(multiModalSessions);

        // 执行多模态检索
        List<String> multiModalQueries = List.of(
            "如何提高代码质量",
            "系统架构设计方法",
            "应用性能监控和优化",
            "API设计规范和工具",
            "技术文档编写技巧"
        );

        for (String query : multiModalQueries) {
            log.info("多模态查询: {}", query);

            MultiModalResult result = associator.multiModalSearch(query);

            log.info("多模态检索结果:");
            log.info("  文本匹配度: {:.2f}", result.getTextRelevance());
            log.info("  代码匹配度: {:.2f}", result.getCodeRelevance());
            log.info("  可视化匹配度: {:.2f}", result.getVisualRelevance());
            log.info("  综合评分: {:.2f}", result.getOverallScore());

            result.getMatchedSessions().forEach(session ->
                log.info("  匹配会话: {} (模态: {})", session.getSessionId(), session.getMatchedModalities()));
        }

        log.info("✅ 多模态会话关联完成");
    }

    /**
     * 语义检索器
     */
    static class SemanticRetriever {

        private final AgentFactory agentFactory;

        public SemanticRetriever(AgentFactory agentFactory) {
            this.agentFactory = agentFactory;
        }

        public void buildSessionIndex(Map<String, String> sessions) {
            log.info("建立语义索引，包含 {} 个会话", sessions.size());

            // 模拟建立语义索引
            sessions.forEach((sessionId, content) ->
                log.debug("索引会话: {} - {}", sessionId, content.substring(0, 50) + "..."));
        }

        public RetrievalResult semanticSearch(String query) {
            // 模拟语义搜索，返回相关性评分
            Map<String, Double> scores = Map.of(
                "session-001", calculateSemanticSimilarity(query, "Java异常处理"),
                "session-002", calculateSemanticSimilarity(query, "Spring配置"),
                "session-003", calculateSemanticSimilarity(query, "数据库优化"),
                "session-004", calculateSemanticSimilarity(query, "微服务架构"),
                "session-005", calculateSemanticSimilarity(query, "敏捷开发"),
                "session-006", calculateSemanticSimilarity(query, "Docker部署")
            );

            // 取前3个最高分的结果
            Map<String, Double> topResults = scores.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .limit(3)
                .collect(java.util.stream.Collectors.toMap(
                    Map.Entry::getKey, Map.Entry::getValue));

            return new RetrievalResult(topResults);
        }

        private double calculateSemanticSimilarity(String query, String content) {
            // 简化的语义相似度计算
            String[] queryWords = query.toLowerCase().split("\\s+");
            String[] contentWords = content.toLowerCase().split("\\s+");

            int matchCount = 0;
            for (String queryWord : queryWords) {
                for (String contentWord : contentWords) {
                    if (contentWord.contains(queryWord) || queryWord.contains(contentWord)) {
                        matchCount++;
                        break;
                    }
                }
            }

            return (double) matchCount / Math.max(queryWords.length, contentWords.length);
        }
    }

    /**
     * 上下文感知推荐器
     */
    static class ContextAwareRecommender {

        private final AgentFactory agentFactory;

        public ContextAwareRecommender(AgentFactory agentFactory) {
            this.agentFactory = agentFactory;
        }

        public void buildUserProfile(UserProfile profile, Map<String, SessionInfo> history) {
            log.info("建立用户画像: {}，历史会话: {}", profile.getRole(), history.size());
        }

        public RecommendationResult recommendSessions(String context) {
            ReActAgent recommender = agentFactory.createRecommendationAgent();

            Msg request = Msg.builder()
                .name("system")
                .role(MsgRole.SYSTEM)
                .content(List.of(TextBlock.builder()
                    .text("基于用户上下文推荐相关会话：\n" + context +
                          "\n\n请分析上下文并推荐最相关的历史会话。")
                    .build()))
                .build();

            Msg result = recommender.call(request).block();

            // 解析推荐结果
            List<SessionRecommendation> recommendations = parseRecommendations(result.getTextContent());

            return new RecommendationResult(recommendations);
        }

        private List<SessionRecommendation> parseRecommendations(String response) {
            // 简化的推荐解析
            return List.of(
                new SessionRecommendation("微服务架构设计", 0.95, "高度相关的架构设计经验"),
                new SessionRecommendation("JVM性能调优", 0.82, "性能优化相关知识"),
                new SessionRecommendation("敏捷开发实践", 0.73, "项目管理方法论"),
                new SessionRecommendation("AWS云服务最佳实践", 0.68, "云平台部署经验")
            );
        }
    }

    /**
     * 多模态关联器
     */
    static class MultiModalAssociator {

        private final AgentFactory agentFactory;

        public MultiModalAssociator(AgentFactory agentFactory) {
            this.agentFactory = agentFactory;
        }

        public void indexMultiModalContent(Map<String, MultiModalContent> sessions) {
            log.info("索引多模态内容，包含 {} 个会话", sessions.size());
        }

        public MultiModalResult multiModalSearch(String query) {
            // 模拟多模态搜索
            double textRelevance = 0.8 + Math.random() * 0.2;
            double codeRelevance = 0.6 + Math.random() * 0.3;
            double visualRelevance = 0.5 + Math.random() * 0.4;

            double overallScore = (textRelevance * 0.5 + codeRelevance * 0.3 + visualRelevance * 0.2);

            List<MatchedSession> matchedSessions = List.of(
                new MatchedSession("code-review-001", List.of("文本", "代码")),
                new MatchedSession("design-doc-002", List.of("文本", "可视化")),
                new MatchedSession("perf-analysis-003", List.of("文本", "代码", "可视化"))
            );

            return new MultiModalResult(textRelevance, codeRelevance, visualRelevance,
                overallScore, matchedSessions);
        }
    }

    // 辅助类
    static class RetrievalResult {
        private final Map<String, Double> topResults;

        public RetrievalResult(Map<String, Double> topResults) {
            this.topResults = topResults;
        }

        public Map<String, Double> getTopResults() { return topResults; }
    }

    static class UserProfile {
        private final String role;
        private final List<String> skills;
        private final String interests;
        private final int experienceYears;

        public UserProfile(String role, List<String> skills, String interests, int experienceYears) {
            this.role = role;
            this.skills = skills;
            this.interests = interests;
            this.experienceYears = experienceYears;
        }

        public String getRole() { return role; }
        public List<String> getSkills() { return skills; }
        public String getInterests() { return interests; }
        public int getExperienceYears() { return experienceYears; }
    }

    static class SessionInfo {
        private final String title;
        private final double engagement;
        private final String category;

        public SessionInfo(String title, double engagement, String category) {
            this.title = title;
            this.engagement = engagement;
            this.category = category;
        }

        public String getTitle() { return title; }
        public double getEngagement() { return engagement; }
        public String getCategory() { return category; }
    }

    static class RecommendationResult {
        private final List<SessionRecommendation> recommendations;

        public RecommendationResult(List<SessionRecommendation> recommendations) {
            this.recommendations = recommendations;
        }

        public List<SessionRecommendation> getRecommendations() { return recommendations; }
    }

    static class SessionRecommendation {
        private final String sessionTitle;
        private final double relevanceScore;
        private final String reason;

        public SessionRecommendation(String sessionTitle, double relevanceScore, String reason) {
            this.sessionTitle = sessionTitle;
            this.relevanceScore = relevanceScore;
            this.reason = reason;
        }

        public String getSessionTitle() { return sessionTitle; }
        public double getRelevanceScore() { return relevanceScore; }
        public String getReason() { return reason; }
    }

    static class MultiModalContent {
        private final String title;
        private final String textContent;
        private final String visualContent;
        private final List<String> tags;

        public MultiModalContent(String title, String textContent, String visualContent, List<String> tags) {
            this.title = title;
            this.textContent = textContent;
            this.visualContent = visualContent;
            this.tags = tags;
        }

        public String getTitle() { return title; }
        public String getTextContent() { return textContent; }
        public String getVisualContent() { return visualContent; }
        public List<String> getTags() { return tags; }
    }

    static class MultiModalResult {
        private final double textRelevance;
        private final double codeRelevance;
        private final double visualRelevance;
        private final double overallScore;
        private final List<MatchedSession> matchedSessions;

        public MultiModalResult(double textRelevance, double codeRelevance, double visualRelevance,
                              double overallScore, List<MatchedSession> matchedSessions) {
            this.textRelevance = textRelevance;
            this.codeRelevance = codeRelevance;
            this.visualRelevance = visualRelevance;
            this.overallScore = overallScore;
            this.matchedSessions = matchedSessions;
        }

        public double getTextRelevance() { return textRelevance; }
        public double getCodeRelevance() { return codeRelevance; }
        public double getVisualRelevance() { return visualRelevance; }
        public double getOverallScore() { return overallScore; }
        public List<MatchedSession> getMatchedSessions() { return matchedSessions; }
    }

    static class MatchedSession {
        private final String sessionId;
        private final List<String> matchedModalities;

        public MatchedSession(String sessionId, List<String> matchedModalities) {
            this.sessionId = sessionId;
            this.matchedModalities = matchedModalities;
        }

        public String getSessionId() { return sessionId; }
        public List<String> getMatchedModalities() { return matchedModalities; }
    }
}
