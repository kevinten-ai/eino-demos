package com.brag.agentscope.examples;

import com.brag.agentscope.config.AgentFactory;
import io.agentscope.core.ReActAgent;
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

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * 动态工作流多Agent示例
 * 演示AgentScope中基于规则的动态工作流构建和执行
 */
@Component
@Profile("dynamic-workflow-example")
@RequiredArgsConstructor
public class DynamicWorkflowExample implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DynamicWorkflowExample.class);

    private final AgentFactory agentFactory;

    @Override
    public void run(String... args) {
        log.info("=== AgentScope 动态工作流多Agent示例 ===");

        try {
            // 示例1: 规则驱动的工作流构建
            demonstrateRuleBasedWorkflow();

            // 示例2: 自适应工作流调整
            demonstrateAdaptiveWorkflow();

            // 示例3: 条件分支工作流
            demonstrateConditionalWorkflow();

            log.info("=== 动态工作流多Agent示例执行完成 ===");

        } catch (Exception e) {
            log.error("动态工作流示例执行失败", e);
        }

        System.exit(0);
    }

    /**
     * 示例1: 规则驱动的工作流构建
     * 演示基于业务规则动态构建和执行工作流
     */
    private void demonstrateRuleBasedWorkflow() {
        log.info("--- 规则驱动的工作流构建示例 ---");

        RuleBasedWorkflowBuilder builder = new RuleBasedWorkflowBuilder(agentFactory);

        // 定义工作流规则
        WorkflowRules rules = new WorkflowRules()
            .addRule("complexity", "high", "add_review_step")
            .addRule("priority", "urgent", "enable_parallel_execution")
            .addRule("team_size", "large", "add_coordination_step");

        // 定义任务上下文
        TaskContext context = new TaskContext()
            .setAttribute("complexity", "high")
            .setAttribute("priority", "urgent")
            .setAttribute("team_size", "large");

        // 构建并执行动态工作流
        String task = "开发企业级用户管理系统：包括用户注册、权限管理、审计日志、安全加固";

        WorkflowResult result = builder.buildAndExecuteWorkflow(task, rules, context);

        log.info("规则驱动工作流结果:");
        log.info("  生成步骤数: {}", result.getGeneratedSteps());
        log.info("  应用规则数: {}", result.getAppliedRules());
        log.info("  执行耗时: {}ms", result.getExecutionTime());
        log.info("  工作流效率: {:.2f}%", result.getWorkflowEfficiency() * 100);

        // 显示执行的步骤
        log.info("执行的工作流步骤:");
        result.getExecutedSteps().forEach((step, status) ->
            log.info("  {}: {}", step, status ? "成功" : "失败"));

        log.info("✅ 规则驱动的工作流构建完成");
    }

    /**
     * 示例2: 自适应工作流调整
     * 演示工作流在执行过程中根据反馈进行动态调整
     */
    private void demonstrateAdaptiveWorkflow() {
        log.info("--- 自适应工作流调整示例 ---");

        AdaptiveWorkflowExecutor adaptiveExecutor = new AdaptiveWorkflowExecutor(agentFactory);

        // 定义初始工作流
        String initialTask = "实施敏捷开发流程转型：培训、工具配置、流程优化";

        // 执行自适应工作流
        AdaptiveResult result = adaptiveExecutor.executeAdaptiveWorkflow(initialTask);

        log.info("自适应工作流结果:");
        log.info("  初始步骤数: {}", result.getInitialSteps());
        log.info("  调整次数: {}", result.getAdaptations());
        log.info("  最终步骤数: {}", result.getFinalSteps());
        log.info("  适应性评分: {:.2f}/5.0", result.getAdaptabilityScore());

        // 显示调整历史
        log.info("工作流调整历史:");
        result.getAdaptationHistory().forEach(adaptation ->
            log.info("  步骤 {}: {} (原因: {})",
                adaptation.getStepIndex(), adaptation.getAction(), adaptation.getReason()));

        log.info("✅ 自适应工作流调整完成");
    }

    /**
     * 示例3: 条件分支工作流
     * 演示基于条件判断的工作流分支执行
     */
    private void demonstrateConditionalWorkflow() {
        log.info("--- 条件分支工作流示例 ---");

        ConditionalWorkflowEngine conditionalEngine = new ConditionalWorkflowEngine(agentFactory);

        // 定义条件分支任务
        String conditionalTask = "根据项目类型选择不同的开发策略：Web应用、移动应用、微服务架构";

        // 执行条件分支工作流
        ConditionalResult result = conditionalEngine.executeConditionalWorkflow(conditionalTask);

        log.info("条件分支工作流结果:");
        log.info("  评估条件数: {}", result.getEvaluatedConditions());
        log.info("  执行分支数: {}", result.getExecutedBranches());
        log.info("  条件准确率: {:.2f}%", result.getConditionAccuracy() * 100);

        // 显示分支执行路径
        log.info("执行的分支路径:");
        result.getExecutionPath().forEach(step ->
            log.info("  {} -> {}", step.getCondition(), step.getBranch()));

        log.info("✅ 条件分支工作流完成");
    }

    /**
     * 规则驱动工作流构建器
     */
    static class RuleBasedWorkflowBuilder {

        private final AgentFactory agentFactory;

        public RuleBasedWorkflowBuilder(AgentFactory agentFactory) {
            this.agentFactory = agentFactory;
        }

        public WorkflowResult buildAndExecuteWorkflow(String task, WorkflowRules rules, TaskContext context) {
            long startTime = System.currentTimeMillis();

            // 基于规则动态构建工作流
            List<String> workflowSteps = buildWorkflowFromRules(task, rules, context);
            int appliedRules = countAppliedRules(rules, context);

            // 执行构建的工作流
            Map<String, Boolean> executionResults = executeWorkflowSteps(workflowSteps);

            long endTime = System.currentTimeMillis();
            double efficiency = calculateWorkflowEfficiency(executionResults);

            return new WorkflowResult(
                workflowSteps.size(),
                appliedRules,
                endTime - startTime,
                efficiency,
                executionResults
            );
        }

        private List<String> buildWorkflowFromRules(String task, WorkflowRules rules, TaskContext context) {
            List<String> steps = new java.util.ArrayList<>();

            // 基础步骤
            steps.add("需求分析");
            steps.add("技术选型");
            steps.add("架构设计");

            // 根据规则添加额外步骤
            if (rules.shouldApply("complexity", "high", context)) {
                steps.add("详细设计审查");
                steps.add("代码审查");
            }

            if (rules.shouldApply("priority", "urgent", context)) {
                steps.add("并行开发");
                steps.add("快速测试");
            }

            if (rules.shouldApply("team_size", "large", context)) {
                steps.add("团队协调会议");
                steps.add("进度同步");
            }

            // 收尾步骤
            steps.add("集成测试");
            steps.add("部署上线");

            return steps;
        }

        private int countAppliedRules(WorkflowRules rules, TaskContext context) {
            return (int) rules.getRules().stream()
                .filter(rule -> rule.shouldApply(context))
                .count();
        }

        private Map<String, Boolean> executeWorkflowSteps(List<String> steps) {
            return steps.stream()
                .collect(Collectors.toMap(
                    step -> step,
                    step -> executeWorkflowStep(step)
                ));
        }

        private boolean executeWorkflowStep(String step) {
            try {
                // 模拟步骤执行
                Thread.sleep(100 + (int)(Math.random() * 200));
                return Math.random() > 0.1; // 90%成功率
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
        }

        private double calculateWorkflowEfficiency(Map<String, Boolean> results) {
            long successfulSteps = results.values().stream().filter(Boolean::booleanValue).count();
            return results.isEmpty() ? 0.0 : (double) successfulSteps / results.size();
        }
    }

    /**
     * 自适应工作流执行器
     */
    static class AdaptiveWorkflowExecutor {

        private final AgentFactory agentFactory;

        public AdaptiveWorkflowExecutor(AgentFactory agentFactory) {
            this.agentFactory = agentFactory;
        }

        public AdaptiveResult executeAdaptiveWorkflow(String task) {
            ReActAgent workflowPlanner = agentFactory.createWorkflowPlannerAgent();

            // 初始规划
            Msg initialPlanRequest = Msg.builder()
                .name("user")
                .role(MsgRole.USER)
                .content(List.of(TextBlock.builder()
                    .text("请为任务制定初始工作流计划：\n" + task)
                    .build()))
                .build();

            Msg initialPlan = workflowPlanner.call(initialPlanRequest).block();
            List<String> workflowSteps = parseWorkflowSteps(initialPlan.getTextContent());
            int initialSteps = workflowSteps.size();

            List<Adaptation> adaptationHistory = new java.util.ArrayList<>();
            int adaptations = 0;

            // 模拟执行和适应过程
            for (int i = 0; i < workflowSteps.size(); i++) {
                String currentStep = workflowSteps.get(i);

                // 模拟执行当前步骤
                boolean stepSuccess = executeStepWithFeedback(currentStep);

                if (!stepSuccess) {
                    // 执行失败，进行适应调整
                    adaptations++;
                    Adaptation adaptation = adaptWorkflowForFailure(workflowPlanner,
                        workflowSteps, i, currentStep);

                    workflowSteps = adaptation.getNewSteps();
                    adaptationHistory.add(adaptation);

                    // 重新执行当前步骤
                    i--; // 重新执行
                }
            }

            double adaptabilityScore = calculateAdaptabilityScore(adaptations, initialSteps);

            return new AdaptiveResult(
                initialSteps,
                adaptations,
                workflowSteps.size(),
                adaptabilityScore,
                adaptationHistory
            );
        }

        private boolean executeStepWithFeedback(String step) {
            // 模拟步骤执行，偶尔失败以触发适应
            return Math.random() > 0.2; // 80%成功率
        }

        private Adaptation adaptWorkflowForFailure(ReActAgent planner, List<String> currentSteps,
                                                 int failedIndex, String failedStep) {
            Msg adaptationRequest = Msg.builder()
                .name("system")
                .role(MsgRole.SYSTEM)
                .content(List.of(TextBlock.builder()
                    .text("工作流执行失败，需要调整：\n" +
                        "失败步骤: " + failedStep + "\n" +
                        "当前工作流: " + String.join(" -> ", currentSteps) + "\n\n" +
                        "请重新规划工作流，避免类似问题。")
                    .build()))
                .build();

            Msg adaptationPlan = planner.call(adaptationRequest).block();
            List<String> newSteps = parseWorkflowSteps(adaptationPlan.getTextContent());

            return new Adaptation(failedIndex, "重新规划步骤", "执行失败需要调整", newSteps);
        }

        private double calculateAdaptabilityScore(int adaptations, int initialSteps) {
            // 适应性评分：调整次数越少越好，但完全没有调整也可能说明缺乏灵活性
            if (adaptations == 0) return 3.0; // 中等评分
            double adaptationRate = (double) adaptations / initialSteps;
            return Math.max(1.0, Math.min(5.0, 5.0 - adaptationRate * 2));
        }

        private List<String> parseWorkflowSteps(String planText) {
            List<String> steps = new java.util.ArrayList<>();
            String[] lines = planText.split("\n");

            for (String line : lines) {
                if (line.matches("\\d+\\..*") || line.contains("步骤")) {
                    String step = line.replaceAll("^\\d+\\.", "").trim();
                    if (!step.isEmpty() && step.length() > 2) {
                        steps.add(step);
                    }
                }
            }

            if (steps.isEmpty()) {
                steps.add("任务分析");
                steps.add("方案设计");
                steps.add("具体实施");
            }

            return steps;
        }
    }

    /**
     * 条件分支工作流引擎
     */
    static class ConditionalWorkflowEngine {

        private final AgentFactory agentFactory;

        public ConditionalWorkflowEngine(AgentFactory agentFactory) {
            this.agentFactory = agentFactory;
        }

        public ConditionalResult executeConditionalWorkflow(String task) {
            ReActAgent decisionMaker = agentFactory.createDecisionMakerAgent();

            // 定义条件和分支
            List<ConditionalBranch> branches = defineBranches();

            List<ExecutionStep> executionPath = new java.util.ArrayList<>();
            int evaluatedConditions = 0;
            int executedBranches = 0;

            for (ConditionalBranch branch : branches) {
                evaluatedConditions++;

                // 评估条件
                Msg conditionRequest = Msg.builder()
                    .name("system")
                    .role(MsgRole.SYSTEM)
                    .content(List.of(TextBlock.builder()
                        .text("请评估以下条件是否满足：\n" +
                            "任务: " + task + "\n" +
                            "条件: " + branch.getCondition() + "\n\n" +
                            "请回答：满足/不满足")
                        .build()))
                    .build();

                Msg conditionResult = decisionMaker.call(conditionRequest).block();
                boolean conditionMet = conditionResult.getTextContent().contains("满足");

                executionPath.add(new ExecutionStep(branch.getCondition(),
                    conditionMet ? branch.getTrueBranch() : branch.getFalseBranch()));

                if (conditionMet) {
                    // 执行对应分支
                    executeBranch(branch.getTrueBranch());
                    executedBranches++;
                }
            }

            double accuracy = calculateConditionAccuracy(executionPath);

            return new ConditionalResult(
                evaluatedConditions,
                executedBranches,
                accuracy,
                executionPath
            );
        }

        private List<ConditionalBranch> defineBranches() {
            return List.of(
                new ConditionalBranch(
                    "项目复杂度是否为企业级",
                    "采用完整开发流程",
                    "采用简化开发流程"
                ),
                new ConditionalBranch(
                    "是否需要高可用性",
                    "添加容灾和备份设计",
                    "使用标准部署方案"
                ),
                new ConditionalBranch(
                    "团队经验是否充足",
                    "采用先进技术栈",
                    "采用稳定技术栈"
                )
            );
        }

        private void executeBranch(String branchName) {
            log.info("执行分支: {}", branchName);
            // 模拟分支执行
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        private double calculateConditionAccuracy(List<ExecutionStep> executionPath) {
            // 模拟条件判断准确性
            return 0.85 + Math.random() * 0.1; // 85-95%准确率
        }
    }

    // 辅助类
    static class WorkflowRules {
        private final List<WorkflowRule> rules = new java.util.ArrayList<>();

        public WorkflowRules addRule(String condition, String value, String action) {
            rules.add(new WorkflowRule(condition, value, action));
            return this;
        }

        public boolean shouldApply(String condition, String value, TaskContext context) {
            return rules.stream().anyMatch(rule ->
                rule.getCondition().equals(condition) &&
                rule.getValue().equals(value) &&
                rule.shouldApply(context));
        }

        public List<WorkflowRule> getRules() { return rules; }
    }

    static class WorkflowRule {
        private final String condition;
        private final String value;
        private final String action;

        public WorkflowRule(String condition, String value, String action) {
            this.condition = condition;
            this.value = value;
            this.action = action;
        }

        public boolean shouldApply(TaskContext context) {
            return value.equals(context.getAttribute(condition));
        }

        public String getCondition() { return condition; }
        public String getValue() { return value; }
        public String getAction() { return action; }
    }

    static class TaskContext {
        private final Map<String, String> attributes = new java.util.HashMap<>();

        public TaskContext setAttribute(String key, String value) {
            attributes.put(key, value);
            return this;
        }

        public String getAttribute(String key) {
            return attributes.get(key);
        }
    }

    static class WorkflowResult {
        private final int generatedSteps;
        private final int appliedRules;
        private final long executionTime;
        private final double workflowEfficiency;
        private final Map<String, Boolean> executedSteps;

        public WorkflowResult(int generatedSteps, int appliedRules, long executionTime,
                            double workflowEfficiency, Map<String, Boolean> executedSteps) {
            this.generatedSteps = generatedSteps;
            this.appliedRules = appliedRules;
            this.executionTime = executionTime;
            this.workflowEfficiency = workflowEfficiency;
            this.executedSteps = executedSteps;
        }

        public int getGeneratedSteps() { return generatedSteps; }
        public int getAppliedRules() { return appliedRules; }
        public long getExecutionTime() { return executionTime; }
        public double getWorkflowEfficiency() { return workflowEfficiency; }
        public Map<String, Boolean> getExecutedSteps() { return executedSteps; }
    }

    static class AdaptiveResult {
        private final int initialSteps;
        private final int adaptations;
        private final int finalSteps;
        private final double adaptabilityScore;
        private final List<Adaptation> adaptationHistory;

        public AdaptiveResult(int initialSteps, int adaptations, int finalSteps,
                            double adaptabilityScore, List<Adaptation> adaptationHistory) {
            this.initialSteps = initialSteps;
            this.adaptations = adaptations;
            this.finalSteps = finalSteps;
            this.adaptabilityScore = adaptabilityScore;
            this.adaptationHistory = adaptationHistory;
        }

        public int getInitialSteps() { return initialSteps; }
        public int getAdaptations() { return adaptations; }
        public int getFinalSteps() { return finalSteps; }
        public double getAdaptabilityScore() { return adaptabilityScore; }
        public List<Adaptation> getAdaptationHistory() { return adaptationHistory; }
    }

    static class Adaptation {
        private final int stepIndex;
        private final String action;
        private final String reason;
        private final List<String> newSteps;

        public Adaptation(int stepIndex, String action, String reason, List<String> newSteps) {
            this.stepIndex = stepIndex;
            this.action = action;
            this.reason = reason;
            this.newSteps = newSteps;
        }

        public int getStepIndex() { return stepIndex; }
        public String getAction() { return action; }
        public String getReason() { return reason; }
        public List<String> getNewSteps() { return newSteps; }
    }

    static class ConditionalResult {
        private final int evaluatedConditions;
        private final int executedBranches;
        private final double conditionAccuracy;
        private final List<ExecutionStep> executionPath;

        public ConditionalResult(int evaluatedConditions, int executedBranches,
                               double conditionAccuracy, List<ExecutionStep> executionPath) {
            this.evaluatedConditions = evaluatedConditions;
            this.executedBranches = executedBranches;
            this.conditionAccuracy = conditionAccuracy;
            this.executionPath = executionPath;
        }

        public int getEvaluatedConditions() { return evaluatedConditions; }
        public int getExecutedBranches() { return executedBranches; }
        public double getConditionAccuracy() { return conditionAccuracy; }
        public List<ExecutionStep> getExecutionPath() { return executionPath; }
    }

    static class ConditionalBranch {
        private final String condition;
        private final String trueBranch;
        private final String falseBranch;

        public ConditionalBranch(String condition, String trueBranch, String falseBranch) {
            this.condition = condition;
            this.trueBranch = trueBranch;
            this.falseBranch = falseBranch;
        }

        public String getCondition() { return condition; }
        public String getTrueBranch() { return trueBranch; }
        public String getFalseBranch() { return falseBranch; }
    }

    static class ExecutionStep {
        private final String condition;
        private final String branch;

        public ExecutionStep(String condition, String branch) {
            this.condition = condition;
            this.branch = branch;
        }

        public String getCondition() { return condition; }
        public String getBranch() { return branch; }
    }
}
