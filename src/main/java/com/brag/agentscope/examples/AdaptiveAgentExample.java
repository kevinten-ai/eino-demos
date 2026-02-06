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
 * 自适应Agent示例
 * 演示AgentScope中基于用户偏好和行为的Agent自适应配置
 */
@Component
@Profile("adaptive-agent-example")
@RequiredArgsConstructor
public class AdaptiveAgentExample implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(AdaptiveAgentExample.class);

    private final AgentFactory agentFactory;

    @Override
    public void run(String... args) {
        log.info("=== AgentScope 自适应Agent示例 ===");

        try {
            // 示例1: 基于用户偏好的Agent适配
            demonstratePreferenceBasedAdaptation();

            // 示例2: 基于交互历史的动态调整
            demonstrateInteractionBasedAdaptation();

            // 示例3: 多维度自适应优化
            demonstrateMultiDimensionalAdaptation();

            log.info("=== 自适应Agent示例执行完成 ===");

        } catch (Exception e) {
            log.error("自适应Agent示例执行失败", e);
        }

        System.exit(0);
    }

    /**
     * 示例1: 基于用户偏好的Agent适配
     * 演示根据用户偏好动态调整Agent行为
     */
    private void demonstratePreferenceBasedAdaptation() {
        log.info("--- 基于用户偏好的Agent适配示例 ---");

        PreferenceBasedAdapter adapter = new PreferenceBasedAdapter(agentFactory);

        // 定义不同类型的用户偏好
        List<UserPreferences> userPreferences = List.of(
            new UserPreferences("新手开发者", Map.of(
                "response_style", "detailed",
                "technical_level", "beginner",
                "learning_pace", "slow",
                "code_examples", "extensive"
            )),
            new UserPreferences("资深架构师", Map.of(
                "response_style", "concise",
                "technical_level", "expert",
                "learning_pace", "fast",
                "code_examples", "minimal"
            )),
            new UserPreferences("产品经理", Map.of(
                "response_style", "business_focused",
                "technical_level", "intermediate",
                "learning_pace", "moderate",
                "code_examples", "conceptual"
            )),
            new UserPreferences("质量保证工程师", Map.of(
                "response_style", "thorough",
                "technical_level", "intermediate",
                "learning_pace", "methodical",
                "code_examples", "testing_focused"
            ))
        );

        // 测试不同偏好下的Agent适配
        String testQuery = "如何优化Java应用的性能？";

        for (UserPreferences preferences : userPreferences) {
            log.info("用户类型: {}", preferences.getUserType());

            AdaptationResult result = adapter.adaptAgentForPreferences(testQuery, preferences);

            log.info("适配结果:");
            log.info("  调整的配置项: {}", result.getAdaptedConfigurations());
            log.info("  Agent响应长度: {} 字符", result.getResponseLength());
            log.info("  使用的技术深度: {}", result.getTechnicalDepth());
            log.info("  包含的代码示例数: {}", result.getCodeExamplesCount());

            // 显示适配后的Agent响应预览
            String preview = result.getResponsePreview();
            log.info("  响应预览: {}...", preview.substring(0, Math.min(100, preview.length())));
        }

        log.info("✅ 基于用户偏好的Agent适配完成");
    }

    /**
     * 示例2: 基于交互历史的动态调整
     * 演示根据用户交互历史动态调整Agent行为
     */
    private void demonstrateInteractionBasedAdaptation() {
        log.info("--- 基于交互历史的动态调整示例 ---");

        InteractionBasedAdapter interactionAdapter = new InteractionBasedAdapter(agentFactory);

        // 模拟用户交互历史
        List<InteractionRecord> interactionHistory = List.of(
            new InteractionRecord("2024-01-01", "解释什么是微服务", "too_basic", "需要更详细的解释"),
            new InteractionRecord("2024-01-02", "Spring Boot配置详解", "too_detailed", "简化一些配置细节"),
            new InteractionRecord("2024-01-03", "数据库设计原则", "just_right", "保持这个水平"),
            new InteractionRecord("2024-01-04", "REST API设计规范", "too_technical", "用更简单的语言"),
            new InteractionRecord("2024-01-05", "敏捷开发实践", "perfect", "继续这个风格")
        );

        interactionAdapter.analyzeInteractionHistory(interactionHistory);

        // 测试基于历史的动态调整
        List<String> testQueries = List.of(
            "什么是设计模式？",
            "Docker基础使用",
            "团队协作工具推荐",
            "代码审查要点"
        );

        for (String query : testQueries) {
            log.info("测试查询: {}", query);

            DynamicAdaptationResult result = interactionAdapter.adaptBasedOnHistory(query);

            log.info("动态调整结果:");
            log.info("  检测到的模式: {}", result.getDetectedPatterns());
            log.info("  调整的策略: {}", result.getAdaptationStrategies());
            log.info("  预期改进度: {:.1f}%", result.getExpectedImprovement() * 100);
            log.info("  调整后的响应风格: {}", result.getAdaptedStyle());
        }

        log.info("✅ 基于交互历史的动态调整完成");
    }

    /**
     * 示例3: 多维度自适应优化
     * 演示综合考虑多个维度的自适应优化
     */
    private void demonstrateMultiDimensionalAdaptation() {
        log.info("--- 多维度自适应优化示例 ---");

        MultiDimensionalOptimizer optimizer = new MultiDimensionalOptimizer(agentFactory);

        // 定义多维度上下文
        MultiDimensionalContext context = new MultiDimensionalContext(
            "高级Java开发者",          // 用户角色
            "项目架构设计阶段",         // 当前任务阶段
            "远程办公环境",            // 工作环境
            "英语技术文档",            // 语言偏好
            "2024年最新技术栈",         // 时间上下文
            List.of("高性能", "可扩展性", "安全性"), // 关注点
            Map.of(                     // 历史偏好
                "response_length", "medium",
                "code_complexity", "advanced",
                "explanation_depth", "detailed"
            )
        );

        optimizer.setContext(context);

        // 测试多维度优化
        List<String> complexQueries = List.of(
            "设计一个支持百万用户的电商平台架构",
            "实现分布式缓存策略的最佳实践",
            "构建微服务间的通信机制",
            "设计高可用的数据库架构"
        );

        for (String query : complexQueries) {
            log.info("复杂查询: {}", query);

            OptimizationResult result = optimizer.optimizeResponse(query);

            log.info("多维度优化结果:");
            log.info("  综合评分: {:.2f}/5.0", result.getOverallScore());
            log.info("  优化维度: {}", result.getOptimizedDimensions());
            log.info("  个性化程度: {:.1f}%", result.getPersonalizationLevel() * 100);
            log.info("  上下文相关性: {:.1f}%", result.getContextRelevance() * 100);

            // 显示维度优化详情
            log.info("  维度优化详情:");
            result.getDimensionOptimizations().forEach((dimension, optimization) ->
                log.info("    {}: {} (影响: {:.1f}%)",
                    dimension, optimization.getStrategy(), optimization.getImpact() * 100));
        }

        log.info("✅ 多维度自适应优化完成");
    }

    /**
     * 偏好基础适配器
     */
    static class PreferenceBasedAdapter {

        private final AgentFactory agentFactory;

        public PreferenceBasedAdapter(AgentFactory agentFactory) {
            this.agentFactory = agentFactory;
        }

        public AdaptationResult adaptAgentForPreferences(String query, UserPreferences preferences) {
            ReActAgent adaptiveAgent = agentFactory.createAdaptiveAgent();

            // 构建个性化提示
            String personalizedPrompt = buildPersonalizedPrompt(query, preferences);

            Msg request = Msg.builder()
                .name("user")
                .role(MsgRole.USER)
                .content(List.of(TextBlock.builder().text(personalizedPrompt).build()))
                .build();

            Msg response = adaptiveAgent.call(request).block();

            // 分析适配结果
            int responseLength = response.getTextContent().length();
            String technicalDepth = analyzeTechnicalDepth(response.getTextContent());
            int codeExamplesCount = countCodeExamples(response.getTextContent());

            Map<String, String> adaptedConfigurations = Map.of(
                "response_style", preferences.getPreferences().get("response_style"),
                "technical_level", preferences.getPreferences().get("technical_level"),
                "learning_pace", preferences.getPreferences().get("learning_pace"),
                "code_examples", preferences.getPreferences().get("code_examples")
            );

            return new AdaptationResult(
                adaptedConfigurations,
                responseLength,
                technicalDepth,
                codeExamplesCount,
                response.getTextContent().substring(0, Math.min(200, response.getTextContent().length()))
            );
        }

        private String buildPersonalizedPrompt(String query, UserPreferences preferences) {
            StringBuilder prompt = new StringBuilder();
            prompt.append("请根据以下用户偏好回答问题：\n");
            prompt.append("用户类型: ").append(preferences.getUserType()).append("\n");

            preferences.getPreferences().forEach((key, value) ->
                prompt.append(key).append(": ").append(value).append("\n"));

            prompt.append("\n问题: ").append(query).append("\n");
            prompt.append("\n请根据用户偏好调整你的回答风格和内容深度。");

            return prompt.toString();
        }

        private String analyzeTechnicalDepth(String response) {
            // 简化的技术深度分析
            if (response.contains("源码") || response.contains("底层实现")) {
                return "深入";
            } else if (response.contains("概念") || response.contains("原理")) {
                return "中等";
            } else {
                return "基础";
            }
        }

        private int countCodeExamples(String response) {
            // 简化的代码示例计数
            long codeBlockCount = response.chars()
                .filter(ch -> ch == '`')
                .count() / 2; // 每对反引号算一个代码块

            return (int) codeBlockCount;
        }
    }

    /**
     * 交互基础适配器
     */
    static class InteractionBasedAdapter {

        private static final Logger log = LoggerFactory.getLogger(InteractionBasedAdapter.class);

        private final AgentFactory agentFactory;
        private final java.util.Map<String, Integer> feedbackPatterns = new java.util.HashMap<>();

        public InteractionBasedAdapter(AgentFactory agentFactory) {
            this.agentFactory = agentFactory;
        }

        public void analyzeInteractionHistory(List<InteractionRecord> history) {
            // 分析交互模式
            history.forEach(record -> {
                feedbackPatterns.merge(record.getFeedback(), 1, Integer::sum);
            });

            log.info("分析了 {} 条交互记录", history.size());
            log.info("反馈模式统计: {}", feedbackPatterns);
        }

        public DynamicAdaptationResult adaptBasedOnHistory(String query) {
            // 基于历史反馈确定调整策略
            List<String> detectedPatterns = detectPatterns();
            List<String> adaptationStrategies = determineStrategies(detectedPatterns);
            double expectedImprovement = calculateExpectedImprovement(adaptationStrategies);
            String adaptedStyle = determineAdaptedStyle(adaptationStrategies);

            return new DynamicAdaptationResult(
                detectedPatterns,
                adaptationStrategies,
                expectedImprovement,
                adaptedStyle
            );
        }

        private List<String> detectPatterns() {
            // 基于反馈模式检测用户偏好
            List<String> patterns = new java.util.ArrayList<>();

            if (feedbackPatterns.getOrDefault("too_basic", 0) > feedbackPatterns.getOrDefault("too_detailed", 0)) {
                patterns.add("偏好详细解释");
            }

            if (feedbackPatterns.getOrDefault("too_technical", 0) > 0) {
                patterns.add("避免过度技术化");
            }

            if (feedbackPatterns.getOrDefault("just_right", 0) > feedbackPatterns.getOrDefault("too_basic", 0)) {
                patterns.add("当前水平合适");
            }

            return patterns;
        }

        private List<String> determineStrategies(List<String> patterns) {
            List<String> strategies = new java.util.ArrayList<>();

            for (String pattern : patterns) {
                switch (pattern) {
                    case "偏好详细解释":
                        strategies.add("增加解释深度");
                        strategies.add("提供更多示例");
                        break;
                    case "避免过度技术化":
                        strategies.add("简化技术术语");
                        strategies.add("增加概念解释");
                        break;
                    case "当前水平合适":
                        strategies.add("保持当前风格");
                        break;
                }
            }

            return strategies;
        }

        private double calculateExpectedImprovement(List<String> strategies) {
            // 基于策略数量估算改进程度
            return Math.min(0.8, strategies.size() * 0.15);
        }

        private String determineAdaptedStyle(List<String> strategies) {
            if (strategies.contains("增加解释深度")) {
                return "详细而易懂";
            } else if (strategies.contains("简化技术术语")) {
                return "简单明了";
            } else {
                return "平衡适中";
            }
        }
    }

    /**
     * 多维度优化器
     */
    static class MultiDimensionalOptimizer {

        private final AgentFactory agentFactory;
        private MultiDimensionalContext context;

        public MultiDimensionalOptimizer(AgentFactory agentFactory) {
            this.agentFactory = agentFactory;
        }

        public void setContext(MultiDimensionalContext context) {
            this.context = context;
        }

        public OptimizationResult optimizeResponse(String query) {
            ReActAgent optimizer = agentFactory.createOptimizationAgent();

            // 构建多维度优化提示
            String optimizationPrompt = buildOptimizationPrompt(query);

            Msg request = Msg.builder()
                .name("user")
                .role(MsgRole.USER)
                .content(List.of(TextBlock.builder().text(optimizationPrompt).build()))
                .build();

            Msg response = optimizer.call(request).block();

            // 分析优化结果
            double overallScore = calculateOverallScore(response.getTextContent());
            List<String> optimizedDimensions = identifyOptimizedDimensions(response.getTextContent());
            double personalizationLevel = calculatePersonalizationLevel(response.getTextContent());
            double contextRelevance = calculateContextRelevance(response.getTextContent());

            Map<String, DimensionOptimization> dimensionOptimizations = analyzeDimensionOptimizations();

            return new OptimizationResult(
                overallScore,
                optimizedDimensions,
                personalizationLevel,
                contextRelevance,
                dimensionOptimizations
            );
        }

        private String buildOptimizationPrompt(String query) {
            StringBuilder prompt = new StringBuilder();

            prompt.append("请根据以下多维度上下文优化回答：\n\n");

            prompt.append("用户角色: ").append(context.getUserRole()).append("\n");
            prompt.append("任务阶段: ").append(context.getTaskPhase()).append("\n");
            prompt.append("工作环境: ").append(context.getWorkEnvironment()).append("\n");
            prompt.append("语言偏好: ").append(context.getLanguagePreference()).append("\n");
            prompt.append("时间上下文: ").append(context.getTimeContext()).append("\n");
            prompt.append("关注点: ").append(String.join(", ", context.getFocusAreas())).append("\n");

            prompt.append("\n历史偏好:\n");
            context.getHistoricalPreferences().forEach((key, value) ->
                prompt.append("- ").append(key).append(": ").append(value).append("\n"));

            prompt.append("\n问题: ").append(query).append("\n\n");
            prompt.append("请提供一个全面优化过的回答，充分考虑以上所有维度。");

            return prompt.toString();
        }

        private double calculateOverallScore(String response) {
            // 简化的综合评分
            double score = 3.0;

            if (response.length() > 500) score += 0.5; // 内容丰富
            if (response.contains("具体") || response.contains("示例")) score += 0.5; // 实用性
            if (response.contains("考虑") || response.contains("根据")) score += 0.5; // 思考深入
            if (response.contains("最新") || response.contains("2024")) score += 0.5; // 时效性

            return Math.min(5.0, score);
        }

        private List<String> identifyOptimizedDimensions(String response) {
            List<String> dimensions = new java.util.ArrayList<>();

            if (response.contains("性能") || response.contains("扩展")) dimensions.add("技术深度");
            if (response.contains("团队") || response.contains("协作")) dimensions.add("协作性");
            if (response.contains("安全") || response.contains("可靠")) dimensions.add("可靠性");
            if (response.contains("用户") || response.contains("体验")) dimensions.add("用户体验");

            return dimensions.isEmpty() ? List.of("通用优化") : dimensions;
        }

        private double calculatePersonalizationLevel(String response) {
            // 简化的个性化程度计算
            return 0.7 + Math.random() * 0.2; // 70-90%
        }

        private double calculateContextRelevance(String response) {
            // 简化的上下文相关性计算
            return 0.8 + Math.random() * 0.15; // 80-95%
        }

        private Map<String, DimensionOptimization> analyzeDimensionOptimizations() {
            return Map.of(
                "技术深度", new DimensionOptimization("提供高级概念解释", 0.85),
                "实用性", new DimensionOptimization("包含具体实施步骤", 0.92),
                "协作性", new DimensionOptimization("强调团队协作方法", 0.78),
                "创新性", new DimensionOptimization("引入最新技术趋势", 0.88)
            );
        }
    }

    // 辅助类
    static class UserPreferences {
        private final String userType;
        private final Map<String, String> preferences;

        public UserPreferences(String userType, Map<String, String> preferences) {
            this.userType = userType;
            this.preferences = preferences;
        }

        public String getUserType() { return userType; }
        public Map<String, String> getPreferences() { return preferences; }
    }

    static class AdaptationResult {
        private final Map<String, String> adaptedConfigurations;
        private final int responseLength;
        private final String technicalDepth;
        private final int codeExamplesCount;
        private final String responsePreview;

        public AdaptationResult(Map<String, String> adaptedConfigurations, int responseLength,
                              String technicalDepth, int codeExamplesCount, String responsePreview) {
            this.adaptedConfigurations = adaptedConfigurations;
            this.responseLength = responseLength;
            this.technicalDepth = technicalDepth;
            this.codeExamplesCount = codeExamplesCount;
            this.responsePreview = responsePreview;
        }

        public Map<String, String> getAdaptedConfigurations() { return adaptedConfigurations; }
        public int getResponseLength() { return responseLength; }
        public String getTechnicalDepth() { return technicalDepth; }
        public int getCodeExamplesCount() { return codeExamplesCount; }
        public String getResponsePreview() { return responsePreview; }
    }

    static class InteractionRecord {
        private final String date;
        private final String query;
        private final String feedback;
        private final String comment;

        public InteractionRecord(String date, String query, String feedback, String comment) {
            this.date = date;
            this.query = query;
            this.feedback = feedback;
            this.comment = comment;
        }

        public String getDate() { return date; }
        public String getQuery() { return query; }
        public String getFeedback() { return feedback; }
        public String getComment() { return comment; }
    }

    static class DynamicAdaptationResult {
        private final List<String> detectedPatterns;
        private final List<String> adaptationStrategies;
        private final double expectedImprovement;
        private final String adaptedStyle;

        public DynamicAdaptationResult(List<String> detectedPatterns, List<String> adaptationStrategies,
                                     double expectedImprovement, String adaptedStyle) {
            this.detectedPatterns = detectedPatterns;
            this.adaptationStrategies = adaptationStrategies;
            this.expectedImprovement = expectedImprovement;
            this.adaptedStyle = adaptedStyle;
        }

        public List<String> getDetectedPatterns() { return detectedPatterns; }
        public List<String> getAdaptationStrategies() { return adaptationStrategies; }
        public double getExpectedImprovement() { return expectedImprovement; }
        public String getAdaptedStyle() { return adaptedStyle; }
    }

    static class MultiDimensionalContext {
        private final String userRole;
        private final String taskPhase;
        private final String workEnvironment;
        private final String languagePreference;
        private final String timeContext;
        private final List<String> focusAreas;
        private final Map<String, String> historicalPreferences;

        public MultiDimensionalContext(String userRole, String taskPhase, String workEnvironment,
                                     String languagePreference, String timeContext,
                                     List<String> focusAreas, Map<String, String> historicalPreferences) {
            this.userRole = userRole;
            this.taskPhase = taskPhase;
            this.workEnvironment = workEnvironment;
            this.languagePreference = languagePreference;
            this.timeContext = timeContext;
            this.focusAreas = focusAreas;
            this.historicalPreferences = historicalPreferences;
        }

        public String getUserRole() { return userRole; }
        public String getTaskPhase() { return taskPhase; }
        public String getWorkEnvironment() { return workEnvironment; }
        public String getLanguagePreference() { return languagePreference; }
        public String getTimeContext() { return timeContext; }
        public List<String> getFocusAreas() { return focusAreas; }
        public Map<String, String> getHistoricalPreferences() { return historicalPreferences; }
    }

    static class OptimizationResult {
        private final double overallScore;
        private final List<String> optimizedDimensions;
        private final double personalizationLevel;
        private final double contextRelevance;
        private final Map<String, DimensionOptimization> dimensionOptimizations;

        public OptimizationResult(double overallScore, List<String> optimizedDimensions,
                                double personalizationLevel, double contextRelevance,
                                Map<String, DimensionOptimization> dimensionOptimizations) {
            this.overallScore = overallScore;
            this.optimizedDimensions = optimizedDimensions;
            this.personalizationLevel = personalizationLevel;
            this.contextRelevance = contextRelevance;
            this.dimensionOptimizations = dimensionOptimizations;
        }

        public double getOverallScore() { return overallScore; }
        public List<String> getOptimizedDimensions() { return optimizedDimensions; }
        public double getPersonalizationLevel() { return personalizationLevel; }
        public double getContextRelevance() { return contextRelevance; }
        public Map<String, DimensionOptimization> getDimensionOptimizations() { return dimensionOptimizations; }
    }

    static class DimensionOptimization {
        private final String strategy;
        private final double impact;

        public DimensionOptimization(String strategy, double impact) {
            this.strategy = strategy;
            this.impact = impact;
        }

        public String getStrategy() { return strategy; }
        public double getImpact() { return impact; }
    }
}
