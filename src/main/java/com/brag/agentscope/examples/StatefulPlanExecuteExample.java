package com.brag.agentscope.examples;

import com.brag.agentscope.config.AgentFactory;
import io.agentscope.core.ReActAgent;
import io.agentscope.core.message.Msg;
import io.agentscope.core.message.MsgRole;
import io.agentscope.core.message.TextBlock;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 状态机Plan-Execute模式示例
 * 演示基于状态机的计划-执行模式，支持状态持久化和流程控制
 */
@Component
@Profile("stateful-plan-execute-example")
@RequiredArgsConstructor
public class StatefulPlanExecuteExample implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(StatefulPlanExecuteExample.class);

    private final AgentFactory agentFactory;

    @Override
    public void run(String... args) {
        log.info("=== AgentScope 状态机Plan-Execute模式示例 ===");

        try {
            // 示例1: 状态驱动的执行流程
            demonstrateStatefulExecution();

            // 示例2: 动态重新规划
            demonstrateDynamicReplanning();

            // 示例3: 错误恢复和重试
            demonstrateErrorRecovery();

            log.info("=== 状态机Plan-Execute模式示例执行完成 ===");

        } catch (Exception e) {
            log.error("状态机Plan-Execute示例执行失败", e);
        }

        System.exit(0);
    }

    /**
     * 示例1: 状态驱动的执行流程
     * 演示基于状态机的完整执行流程
     */
    private void demonstrateStatefulExecution() {
        log.info("--- 状态驱动的执行流程示例 ---");

        // 创建状态机执行器
        StatefulPlanExecutor executor = new StatefulPlanExecutor(agentFactory);

        // 定义复杂任务
        String complexTask = "组织一次线上技术分享活动：包括主题确定、演讲者邀请、日程安排、平台设置、宣传推广";

        // 执行状态机流程
        ExecutionResult result = executor.executeWithStateMachine(complexTask);

        log.info("执行状态机结果:");
        log.info("  总步骤数: {}", result.getTotalSteps());
        log.info("  完成步骤数: {}", result.getCompletedSteps());
        log.info("  是否成功: {}", result.isSuccessful());
        log.info("  执行耗时: {}ms", result.getExecutionTime());

        if (result.isSuccessful()) {
            log.info("✅ 状态驱动执行流程成功完成");
        } else {
            log.warn("⚠️ 状态驱动执行流程未完全成功");
        }
    }

    /**
     * 示例2: 动态重新规划
     * 演示基于执行反馈的动态规划调整
     */
    private void demonstrateDynamicReplanning() {
        log.info("--- 动态重新规划示例 ---");

        AdaptivePlanExecutor adaptiveExecutor = new AdaptivePlanExecutor(agentFactory);

        // 定义需要调整的复杂任务
        String adaptiveTask = "开发一个移动应用：从概念到原型设计";

        // 执行自适应规划流程
        AdaptiveExecutionResult result = adaptiveExecutor.executeWithAdaptation(adaptiveTask);

        log.info("自适应执行结果:");
        log.info("  初始计划步骤: {}", result.getInitialPlanSteps());
        log.info("  实际执行步骤: {}", result.getActualExecutedSteps());
        log.info("  重新规划次数: {}", result.getReplanningCount());
        log.info("  成功率: {:.2f}%", result.getSuccessRate() * 100);

        // 显示重新规划的历史
        log.info("重新规划历史:");
        result.getReplanningHistory().forEach((step, reason) ->
            log.info("  步骤 {}: {}", step, reason));

        log.info("✅ 动态重新规划完成");
    }

    /**
     * 示例3: 错误恢复和重试
     * 演示错误处理和自动恢复机制
     */
    private void demonstrateErrorRecovery() {
        log.info("--- 错误恢复和重试示例 ---");

        ResilientPlanExecutor resilientExecutor = new ResilientPlanExecutor(agentFactory);

        // 定义容易出错的复杂任务
        String errorProneTask = "集成第三方API并处理各种异常情况";

        // 执行带有错误恢复的流程
        ResilientExecutionResult result = resilientExecutor.executeWithResilience(errorProneTask);

        log.info("弹性执行结果:");
        log.info("  总重试次数: {}", result.getTotalRetries());
        log.info("  成功步骤数: {}", result.getSuccessfulSteps());
        log.info("  失败步骤数: {}", result.getFailedSteps());
        log.info("  恢复成功率: {:.2f}%", result.getRecoveryRate() * 100);

        // 显示错误和恢复历史
        log.info("错误恢复历史:");
        result.getErrorHistory().forEach(error ->
            log.info("  {}: {} -> {}", error.getStep(), error.getError(), error.getRecoveryAction()));

        log.info("✅ 错误恢复和重试完成");
    }

    /**
     * 状态机执行器
     */
    static class StatefulPlanExecutor {

        private final AgentFactory agentFactory;
        private final Map<String, ExecutionState> stateStore;

        public StatefulPlanExecutor(AgentFactory agentFactory) {
            this.agentFactory = agentFactory;
            this.stateStore = new ConcurrentHashMap<>();
        }

        public ExecutionResult executeWithStateMachine(String task) {
            String executionId = "exec-" + System.currentTimeMillis();
            ExecutionState state = new ExecutionState(executionId, task);
            stateStore.put(executionId, state);

            long startTime = System.currentTimeMillis();

            try {
                // 状态1: 初始化
                state.setCurrentState(State.INITIALIZING);
                initializeExecution(state);

                // 状态2: 规划
                state.setCurrentState(State.PLANNING);
                createPlan(state);

                // 状态3: 执行
                state.setCurrentState(State.EXECUTING);
                executePlan(state);

                // 状态4: 完成
                state.setCurrentState(State.COMPLETED);

                long endTime = System.currentTimeMillis();
                return new ExecutionResult(
                    state.getPlanSteps().size(),
                    state.getCompletedSteps().size(),
                    true,
                    endTime - startTime
                );

            } catch (Exception e) {
                state.setCurrentState(State.FAILED);
                state.setError(e.getMessage());

                long endTime = System.currentTimeMillis();
                return new ExecutionResult(
                    state.getPlanSteps().size(),
                    state.getCompletedSteps().size(),
                    false,
                    endTime - startTime
                );
            }
        }

        private void initializeExecution(ExecutionState state) {
            log.info("初始化执行: {}", state.getExecutionId());
            // 初始化执行环境
        }

        private void createPlan(ExecutionState state) {
            ReActAgent planner = agentFactory.createPlanningAgent();

            Msg planRequest = Msg.builder()
                .name("system")
                .role(MsgRole.SYSTEM)
                .content(List.of(TextBlock.builder()
                    .text("请为以下任务制定详细的执行步骤：\n" + state.getTask())
                    .build()))
                .build();

            Msg planResponse = planner.call(planRequest).block();
            List<String> steps = parseSteps(planResponse.getTextContent());

            state.setPlanSteps(steps);
            log.info("制定计划完成，共 {} 步骤", steps.size());
        }

        private void executePlan(ExecutionState state) {
            ReActAgent executor = agentFactory.createExecutionAgent();

            for (int i = 0; i < state.getPlanSteps().size(); i++) {
                String step = state.getPlanSteps().get(i);
                log.info("执行步骤 {}/{}: {}", i + 1, state.getPlanSteps().size(), step);

                try {
                    Msg executionRequest = Msg.builder()
                        .name("system")
                        .role(MsgRole.SYSTEM)
                        .content(List.of(TextBlock.builder()
                            .text("请执行以下步骤：\n" + step)
                            .build()))
                        .build();

                    Msg result = executor.call(executionRequest).block();
                    state.addCompletedStep(step, result.getTextContent());

                } catch (Exception e) {
                    log.warn("步骤执行失败: {}", step, e);
                    state.addFailedStep(step, e.getMessage());
                    // 在实际实现中可能需要决定是否继续
                }
            }
        }

        private List<String> parseSteps(String planText) {
            // 简单的步骤解析
            List<String> steps = new java.util.ArrayList<>();
            String[] lines = planText.split("\n");

            for (String line : lines) {
                if (line.matches("\\d+\\..*")) {
                    steps.add(line.substring(line.indexOf(".") + 1).trim());
                }
            }

            if (steps.isEmpty()) {
                steps.add("分析任务需求");
                steps.add("制定实施计划");
                steps.add("执行具体任务");
            }

            return steps;
        }
    }

    /**
     * 自适应执行器
     */
    static class AdaptivePlanExecutor {

        private final AgentFactory agentFactory;

        public AdaptivePlanExecutor(AgentFactory agentFactory) {
            this.agentFactory = agentFactory;
        }

        public AdaptiveExecutionResult executeWithAdaptation(String task) {
            ReActAgent planner = agentFactory.createPlanningAgent();
            ReActAgent executor = agentFactory.createExecutionAgent();

            // 初始规划
            Msg initialPlan = createPlan(planner, task);
            List<String> planSteps = parseSteps(initialPlan.getTextContent());

            int replanningCount = 0;
            List<String> executedSteps = new java.util.ArrayList<>();
            Map<String, String> replanningHistory = new java.util.HashMap<>();

            // 执行并适应
            for (int i = 0; i < planSteps.size(); i++) {
                String step = planSteps.get(i);
                log.info("执行步骤: {}", step);

                Msg result = executeStep(executor, step, task);
                executedSteps.add(step);

                // 检查是否需要重新规划
                if (shouldReplan(result, i, planSteps.size())) {
                    replanningCount++;
                    String reason = analyzeReplanReason(result);

                    log.info("检测到需要重新规划: {}", reason);

                    // 重新规划剩余步骤
                    Msg newPlan = replan(planner, task, executedSteps, reason);
                    List<String> newSteps = parseSteps(newPlan.getTextContent());

                    // 更新计划
                    planSteps = new java.util.ArrayList<>();
                    planSteps.addAll(executedSteps);
                    planSteps.addAll(newSteps);

                    replanningHistory.put("步骤" + (i + 1), reason);

                    i--; // 重新执行当前步骤
                }
            }

            return new AdaptiveExecutionResult(
                planSteps.size(),
                executedSteps.size(),
                replanningCount,
                (double) executedSteps.size() / planSteps.size(),
                replanningHistory
            );
        }

        private Msg createPlan(ReActAgent planner, String task) {
            Msg request = Msg.builder()
                .name("user")
                .role(MsgRole.USER)
                .content(List.of(TextBlock.builder()
                    .text("制定执行计划：" + task)
                    .build()))
                .build();
            return planner.call(request).block();
        }

        private Msg executeStep(ReActAgent executor, String step, String originalTask) {
            Msg request = Msg.builder()
                .name("system")
                .role(MsgRole.SYSTEM)
                .content(List.of(TextBlock.builder()
                    .text("执行步骤（原任务：" + originalTask + "）：\n" + step)
                    .build()))
                .build();
            return executor.call(request).block();
        }

        private boolean shouldReplan(Msg result, int currentStep, int totalSteps) {
            // 简单的重新规划判断逻辑
            String content = result.getTextContent().toLowerCase();
            return content.contains("失败") ||
                   content.contains("错误") ||
                   content.contains("无法") ||
                   (currentStep > totalSteps * 0.7 && !content.contains("完成"));
        }

        private String analyzeReplanReason(Msg result) {
            String content = result.getTextContent().toLowerCase();
            if (content.contains("失败") || content.contains("错误")) {
                return "执行遇到错误";
            } else if (content.contains("无法")) {
                return "无法完成当前步骤";
            } else {
                return "执行进度不佳，需要调整计划";
            }
        }

        private Msg replan(ReActAgent planner, String originalTask,
                          List<String> executedSteps, String reason) {
            String context = "原任务：" + originalTask + "\n" +
                           "已执行步骤：" + String.join("、", executedSteps) + "\n" +
                           "重新规划原因：" + reason;

            Msg request = Msg.builder()
                .name("system")
                .role(MsgRole.SYSTEM)
                .content(List.of(TextBlock.builder()
                    .text("基于当前执行情况重新规划剩余步骤：\n" + context)
                    .build()))
                .build();

            return planner.call(request).block();
        }

        private List<String> parseSteps(String planText) {
            List<String> steps = new java.util.ArrayList<>();
            String[] lines = planText.split("\n");

            for (String line : lines) {
                if (line.matches("\\d+\\..*")) {
                    steps.add(line.substring(line.indexOf(".") + 1).trim());
                }
            }

            return steps.isEmpty() ? List.of("执行剩余任务") : steps;
        }
    }

    /**
     * 弹性执行器
     */
    static class ResilientPlanExecutor {

        private final AgentFactory agentFactory;

        public ResilientPlanExecutor(AgentFactory agentFactory) {
            this.agentFactory = agentFactory;
        }

        public ResilientExecutionResult executeWithResilience(String task) {
            ReActAgent executor = agentFactory.createExecutionAgent();

            // 模拟有风险的执行步骤
            List<String> riskySteps = List.of(
                "连接外部API",
                "处理大数据",
                "执行复杂计算",
                "调用第三方服务"
            );

            int totalRetries = 0;
            int successfulSteps = 0;
            int failedSteps = 0;
            List<ErrorRecord> errorHistory = new java.util.ArrayList<>();

            for (String step : riskySteps) {
                int retryCount = 0;
                boolean success = false;

                while (retryCount < 3 && !success) {
                    try {
                        log.info("执行步骤 '{}' (尝试 {}/{})", step, retryCount + 1, 3);

                        Msg result = executeStepWithRisk(executor, step);
                        String content = result.getTextContent();

                        // 模拟随机失败
                        if (Math.random() < 0.4) { // 40%失败率
                            throw new RuntimeException("模拟执行失败");
                        }

                        success = true;
                        successfulSteps++;
                        log.info("步骤 '{}' 执行成功", step);

                    } catch (Exception e) {
                        retryCount++;
                        totalRetries++;

                        if (retryCount >= 3) {
                            failedSteps++;
                            errorHistory.add(new ErrorRecord(step, e.getMessage(), "放弃重试"));
                            log.error("步骤 '{}' 执行最终失败", step);
                        } else {
                            errorHistory.add(new ErrorRecord(step, e.getMessage(), "重试"));
                            log.warn("步骤 '{}' 执行失败，将重试 (尝试 {}/{})", step, retryCount, 3);

                            // 等待后重试
                            try {
                                Thread.sleep(1000 * retryCount);
                            } catch (InterruptedException ie) {
                                Thread.currentThread().interrupt();
                            }
                        }
                    }
                }
            }

            double recoveryRate = (double) successfulSteps / riskySteps.size();

            return new ResilientExecutionResult(
                totalRetries,
                successfulSteps,
                failedSteps,
                recoveryRate,
                errorHistory
            );
        }

        private Msg executeStepWithRisk(ReActAgent executor, String step) {
            Msg request = Msg.builder()
                .name("system")
                .role(MsgRole.SYSTEM)
                .content(List.of(TextBlock.builder()
                    .text("执行以下步骤（可能存在风险）：\n" + step)
                    .build()))
                .build();

            return executor.call(request).block();
        }
    }

    // 辅助类和枚举
    enum State {
        INITIALIZING, PLANNING, EXECUTING, COMPLETED, FAILED
    }

    static class ExecutionState {
        private final String executionId;
        private final String task;
        private State currentState;
        private List<String> planSteps = new java.util.ArrayList<>();
        private Map<String, String> completedSteps = new java.util.HashMap<>();
        private Map<String, String> failedSteps = new java.util.HashMap<>();
        private String error;

        public ExecutionState(String executionId, String task) {
            this.executionId = executionId;
            this.task = task;
            this.currentState = State.INITIALIZING;
        }

        // Getters and setters
        public String getExecutionId() { return executionId; }
        public String getTask() { return task; }
        public State getCurrentState() { return currentState; }
        public void setCurrentState(State state) { this.currentState = state; }
        public List<String> getPlanSteps() { return planSteps; }
        public void setPlanSteps(List<String> planSteps) { this.planSteps = planSteps; }
        public Map<String, String> getCompletedSteps() { return completedSteps; }
        public void addCompletedStep(String step, String result) { completedSteps.put(step, result); }
        public Map<String, String> getFailedSteps() { return failedSteps; }
        public void addFailedStep(String step, String error) { failedSteps.put(step, error); }
        public String getError() { return error; }
        public void setError(String error) { this.error = error; }
    }

    static class ExecutionResult {
        private final int totalSteps;
        private final int completedSteps;
        private final boolean successful;
        private final long executionTime;

        public ExecutionResult(int totalSteps, int completedSteps, boolean successful, long executionTime) {
            this.totalSteps = totalSteps;
            this.completedSteps = completedSteps;
            this.successful = successful;
            this.executionTime = executionTime;
        }

        public int getTotalSteps() { return totalSteps; }
        public int getCompletedSteps() { return completedSteps; }
        public boolean isSuccessful() { return successful; }
        public long getExecutionTime() { return executionTime; }
    }

    static class AdaptiveExecutionResult {
        private final int initialPlanSteps;
        private final int actualExecutedSteps;
        private final int replanningCount;
        private final double successRate;
        private final Map<String, String> replanningHistory;

        public AdaptiveExecutionResult(int initialPlanSteps, int actualExecutedSteps,
                                     int replanningCount, double successRate,
                                     Map<String, String> replanningHistory) {
            this.initialPlanSteps = initialPlanSteps;
            this.actualExecutedSteps = actualExecutedSteps;
            this.replanningCount = replanningCount;
            this.successRate = successRate;
            this.replanningHistory = replanningHistory;
        }

        public int getInitialPlanSteps() { return initialPlanSteps; }
        public int getActualExecutedSteps() { return actualExecutedSteps; }
        public int getReplanningCount() { return replanningCount; }
        public double getSuccessRate() { return successRate; }
        public Map<String, String> getReplanningHistory() { return replanningHistory; }
    }

    static class ResilientExecutionResult {
        private final int totalRetries;
        private final int successfulSteps;
        private final int failedSteps;
        private final double recoveryRate;
        private final List<ErrorRecord> errorHistory;

        public ResilientExecutionResult(int totalRetries, int successfulSteps, int failedSteps,
                                      double recoveryRate, List<ErrorRecord> errorHistory) {
            this.totalRetries = totalRetries;
            this.successfulSteps = successfulSteps;
            this.failedSteps = failedSteps;
            this.recoveryRate = recoveryRate;
            this.errorHistory = errorHistory;
        }

        public int getTotalRetries() { return totalRetries; }
        public int getSuccessfulSteps() { return successfulSteps; }
        public int getFailedSteps() { return failedSteps; }
        public double getRecoveryRate() { return recoveryRate; }
        public List<ErrorRecord> getErrorHistory() { return errorHistory; }
    }

    static class ErrorRecord {
        private final String step;
        private final String error;
        private final String recoveryAction;

        public ErrorRecord(String step, String error, String recoveryAction) {
            this.step = step;
            this.error = error;
            this.recoveryAction = recoveryAction;
        }

        public String getStep() { return step; }
        public String getError() { return error; }
        public String getRecoveryAction() { return recoveryAction; }

        @Override
        public String toString() {
            return step + ": " + error + " -> " + recoveryAction;
        }
    }
}
