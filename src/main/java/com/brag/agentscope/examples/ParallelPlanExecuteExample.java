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
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * 并行Plan-Execute模式示例
 * 演示AgentScope中支持并行执行的计划-执行模式
 */
@Component
@Profile("parallel-plan-execute-example")
@RequiredArgsConstructor
public class ParallelPlanExecuteExample implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(ParallelPlanExecuteExample.class);

    private final AgentFactory agentFactory;

    @Override
    public void run(String... args) {
        log.info("=== AgentScope 并行Plan-Execute模式示例 ===");

        try {
            // 示例1: 任务分解并行执行
            demonstrateParallelTaskExecution();

            // 示例2: 流水线并行处理
            demonstratePipelineParallelProcessing();

            // 示例3: 自适应并行度控制
            demonstrateAdaptiveParallelism();

            log.info("=== 并行Plan-Execute模式示例执行完成 ===");

        } catch (Exception e) {
            log.error("并行Plan-Execute示例执行失败", e);
        }

        System.exit(0);
    }

    /**
     * 示例1: 任务分解并行执行
     * 演示将复杂任务分解为可并行执行的子任务
     */
    private void demonstrateParallelTaskExecution() {
        log.info("--- 任务分解并行执行示例 ---");

        ParallelTaskExecutor executor = new ParallelTaskExecutor(agentFactory);

        // 定义可并行化的复杂任务
        String complexTask = "进行市场分析：包括竞争对手分析、用户调研、趋势预测、市场规模评估";

        // 执行并行任务处理
        ParallelExecutionResult result = executor.executeParallelTasks(complexTask);

        log.info("并行执行结果:");
        log.info("  总任务数: {}", result.getTotalTasks());
        log.info("  并行度: {}", result.getParallelism());
        log.info("  执行耗时: {}ms", result.getExecutionTime());
        log.info("  加速比: {:.2f}x", result.getSpeedupRatio());

        // 显示各任务结果
        log.info("任务执行详情:");
        result.getTaskResults().forEach((task, taskResult) ->
            log.info("  {}: {}ms - {}", task, taskResult.getExecutionTime(),
                taskResult.getSuccess() ? "成功" : "失败"));

        log.info("✅ 任务分解并行执行完成");
    }

    /**
     * 示例2: 流水线并行处理
     * 演示流水线模式的并行处理，每个阶段可以并行处理多个项目
     */
    private void demonstratePipelineParallelProcessing() {
        log.info("--- 流水线并行处理示例 ---");

        PipelineProcessor processor = new PipelineProcessor(agentFactory);

        // 定义流水线任务
        List<String> projects = List.of(
            "电商网站重构",
            "移动应用开发",
            "数据仓库建设",
            "API服务升级",
            "测试环境搭建"
        );

        // 执行流水线处理
        PipelineResult result = processor.processPipeline(projects);

        log.info("流水线处理结果:");
        log.info("  处理项目数: {}", result.getProcessedItems());
        log.info("  流水线阶段数: {}", result.getPipelineStages());
        log.info("  总耗时: {}ms", result.getTotalTime());
        log.info("  平均项目耗时: {}ms", result.getAverageItemTime());

        // 显示流水线效率
        log.info("流水线效率分析:");
        result.getStageMetrics().forEach((stage, metrics) ->
            log.info("  {}: 并行度={}, 吞吐量={}/s, 利用率={:.1f}%",
                stage, metrics.getParallelism(),
                String.format("%.2f", metrics.getThroughput()),
                metrics.getUtilization() * 100));

        log.info("✅ 流水线并行处理完成");
    }

    /**
     * 示例3: 自适应并行度控制
     * 演示根据系统负载和任务特点动态调整并行度
     */
    private void demonstrateAdaptiveParallelism() {
        log.info("--- 自适应并行度控制示例 ---");

        AdaptiveParallelExecutor adaptiveExecutor = new AdaptiveParallelExecutor(agentFactory);

        // 定义不同类型的任务
        List<TaskDefinition> tasks = List.of(
            new TaskDefinition("CPU密集型任务", TaskType.CPU_INTENSIVE),
            new TaskDefinition("IO密集型任务", TaskType.IO_INTENSIVE),
            new TaskDefinition("内存密集型任务", TaskType.MEMORY_INTENSIVE),
            new TaskDefinition("网络密集型任务", TaskType.NETWORK_INTENSIVE),
            new TaskDefinition("混合型任务", TaskType.MIXED)
        );

        // 执行自适应并行处理
        AdaptiveResult result = adaptiveExecutor.executeWithAdaptiveParallelism(tasks);

        log.info("自适应并行执行结果:");
        log.info("  任务总数: {}", result.getTotalTasks());
        log.info("  平均并行度: {:.1f}", result.getAverageParallelism());
        log.info("  并行度调整次数: {}", result.getParallelismAdjustments());
        log.info("  系统资源利用率: {:.1f}%", result.getResourceUtilization() * 100);

        // 显示不同任务类型的并行度选择
        log.info("任务类型并行度分析:");
        result.getTaskTypeMetrics().forEach((taskType, metrics) ->
            log.info("  {}: 推荐并行度={}, 实际效率={:.1f}%",
                taskType, metrics.getRecommendedParallelism(),
                metrics.getActualEfficiency() * 100));

        log.info("✅ 自适应并行度控制完成");
    }

    /**
     * 并行任务执行器
     */
    static class ParallelTaskExecutor {

        private final AgentFactory agentFactory;

        public ParallelTaskExecutor(AgentFactory agentFactory) {
            this.agentFactory = agentFactory;
        }

        public ParallelExecutionResult executeParallelTasks(String complexTask) {
            long startTime = System.currentTimeMillis();

            // 任务分解
            List<String> subTasks = decomposeTask(complexTask);
            int parallelism = Math.min(subTasks.size(), Runtime.getRuntime().availableProcessors());

            log.info("将复杂任务分解为 {} 个子任务，使用并行度 {}", subTasks.size(), parallelism);

            // 并行执行子任务
            List<CompletableFuture<TaskResult>> futures = subTasks.stream()
                .map(task -> CompletableFuture.supplyAsync(() -> executeSubTask(task)))
                .collect(Collectors.toList());

            // 等待所有任务完成
            List<TaskResult> results = futures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());

            long endTime = System.currentTimeMillis();

            // 计算加速比（假设串行执行时间是并行时间的parallelism倍）
            double speedupRatio = (double) subTasks.size() / parallelism;

            Map<String, TaskResult> taskResults = subTasks.stream()
                .collect(Collectors.toMap(task -> task, task ->
                    results.stream()
                        .filter(r -> r.getTask().equals(task))
                        .findFirst()
                        .orElse(new TaskResult(task, false, 0))));

            return new ParallelExecutionResult(
                subTasks.size(),
                parallelism,
                endTime - startTime,
                speedupRatio,
                taskResults
            );
        }

        private List<String> decomposeTask(String complexTask) {
            // 简单的任务分解逻辑
            if (complexTask.contains("市场分析")) {
                return List.of(
                    "分析竞争对手策略",
                    "进行用户调研和访谈",
                    "预测市场发展趋势",
                    "评估市场规模和潜力"
                );
            } else {
                return List.of(
                    "任务分析",
                    "方案设计",
                    "具体实施",
                    "结果验证"
                );
            }
        }

        private TaskResult executeSubTask(String task) {
            long taskStartTime = System.currentTimeMillis();

            try {
                ReActAgent executor = agentFactory.createExecutionAgent();

                Msg request = Msg.builder()
                    .name("system")
                    .role(MsgRole.SYSTEM)
                    .content(List.of(TextBlock.builder()
                        .text("执行子任务：" + task)
                        .build()))
                    .build();

                Msg result = executor.call(request).block();

                long taskEndTime = System.currentTimeMillis();

                log.info("子任务 '{}' 执行完成，耗时 {}ms", task, taskEndTime - taskStartTime);

                return new TaskResult(task, true, taskEndTime - taskStartTime);

            } catch (Exception e) {
                long taskEndTime = System.currentTimeMillis();
                log.warn("子任务 '{}' 执行失败", task, e);

                return new TaskResult(task, false, taskEndTime - taskStartTime);
            }
        }
    }

    /**
     * 流水线处理器
     */
    static class PipelineProcessor {

        private final AgentFactory agentFactory;

        public PipelineProcessor(AgentFactory agentFactory) {
            this.agentFactory = agentFactory;
        }

        public PipelineResult processPipeline(List<String> projects) {
            long startTime = System.currentTimeMillis();

            // 定义流水线阶段
            List<PipelineStage> stages = List.of(
                new PipelineStage("需求分析", 2),
                new PipelineStage("设计阶段", 3),
                new PipelineStage("开发阶段", 4),
                new PipelineStage("测试阶段", 2),
                new PipelineStage("部署阶段", 1)
            );

            Map<String, StageMetrics> stageMetrics = new java.util.HashMap<>();

            // 执行流水线处理
            for (PipelineStage stage : stages) {
                long stageStartTime = System.currentTimeMillis();

                log.info("执行流水线阶段: {} (并行度: {})", stage.getName(), stage.getParallelism());

                // 并行处理所有项目在当前阶段
                List<CompletableFuture<Void>> stageFutures = projects.stream()
                    .map(project -> CompletableFuture.runAsync(() ->
                        processProjectInStage(project, stage)))
                    .collect(Collectors.toList());

                // 等待阶段完成
                stageFutures.forEach(CompletableFuture::join);

                long stageEndTime = System.currentTimeMillis();
                long stageTime = stageEndTime - stageStartTime;

                // 计算阶段指标
                double throughput = (double) projects.size() / (stageTime / 1000.0);
                double utilization = Math.min(1.0, (double) projects.size() / stage.getParallelism());

                stageMetrics.put(stage.getName(), new StageMetrics(
                    stage.getParallelism(),
                    throughput,
                    utilization
                ));

                log.info("阶段 '{}' 完成，耗时 {}ms", stage.getName(), stageTime);
            }

            long endTime = System.currentTimeMillis();
            long totalTime = endTime - startTime;
            long averageItemTime = totalTime / projects.size();

            return new PipelineResult(
                projects.size(),
                stages.size(),
                totalTime,
                averageItemTime,
                stageMetrics
            );
        }

        private void processProjectInStage(String project, PipelineStage stage) {
            try {
                ReActAgent processor = agentFactory.createSpecialistAgent(stage.getName());

                Msg request = Msg.builder()
                    .name("system")
                    .role(MsgRole.SYSTEM)
                    .content(List.of(TextBlock.builder()
                        .text(String.format("在%s阶段处理项目：%s", stage.getName(), project))
                        .build()))
                    .build();

                processor.call(request).block();

                // 模拟处理时间
                Thread.sleep(100 + (int)(Math.random() * 200));

            } catch (Exception e) {
                log.warn("项目 '{}' 在阶段 '{}' 处理失败", project, stage.getName(), e);
            }
        }
    }

    /**
     * 自适应并行执行器
     */
    static class AdaptiveParallelExecutor {

        private final AgentFactory agentFactory;

        public AdaptiveParallelExecutor(AgentFactory agentFactory) {
            this.agentFactory = agentFactory;
        }

        public AdaptiveResult executeWithAdaptiveParallelism(List<TaskDefinition> tasks) {
            List<Integer> parallelismHistory = new java.util.ArrayList<>();
            Map<TaskType, TaskTypeMetrics> typeMetrics = new java.util.HashMap<>();
            double totalResourceUtilization = 0.0;
            int adjustmentCount = 0;

            for (TaskDefinition task : tasks) {
                // 根据任务类型确定推荐并行度
                int recommendedParallelism = recommendParallelism(task.getType());
                parallelismHistory.add(recommendedParallelism);

                log.info("执行任务 '{}' (类型: {}), 推荐并行度: {}",
                    task.getName(), task.getType(), recommendedParallelism);

                // 执行任务
                double actualEfficiency = executeTaskWithParallelism(task, recommendedParallelism);

                // 记录类型指标
                typeMetrics.computeIfAbsent(task.getType(), k -> new TaskTypeMetrics())
                    .addExecution(recommendedParallelism, actualEfficiency);

                // 模拟资源利用率
                totalResourceUtilization += actualEfficiency;

                // 检查是否需要调整并行度
                if (shouldAdjustParallelism(actualEfficiency)) {
                    adjustmentCount++;
                    int newParallelism = adjustParallelism(recommendedParallelism, actualEfficiency);
                    log.info("调整并行度: {} -> {}", recommendedParallelism, newParallelism);
                }
            }

            double averageParallelism = parallelismHistory.stream()
                .mapToInt(Integer::intValue)
                .average()
                .orElse(1.0);

            double avgResourceUtilization = totalResourceUtilization / tasks.size();

            return new AdaptiveResult(
                tasks.size(),
                averageParallelism,
                adjustmentCount,
                avgResourceUtilization,
                typeMetrics
            );
        }

        private int recommendParallelism(TaskType type) {
            switch (type) {
                case CPU_INTENSIVE: return Math.max(1, Runtime.getRuntime().availableProcessors() / 2);
                case IO_INTENSIVE: return Runtime.getRuntime().availableProcessors() * 2;
                case MEMORY_INTENSIVE: return Math.max(1, Runtime.getRuntime().availableProcessors() / 4);
                case NETWORK_INTENSIVE: return Runtime.getRuntime().availableProcessors();
                case MIXED: return Runtime.getRuntime().availableProcessors();
                default: return 1;
            }
        }

        private double executeTaskWithParallelism(TaskDefinition task, int parallelism) {
            long startTime = System.currentTimeMillis();

            // 模拟并行执行
            List<CompletableFuture<Void>> futures = new java.util.ArrayList<>();
            for (int i = 0; i < parallelism; i++) {
                futures.add(CompletableFuture.runAsync(() -> {
                    try {
                        // 模拟任务执行时间
                        Thread.sleep(50 + (int)(Math.random() * 100));
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }));
            }

            // 等待所有并行任务完成
            futures.forEach(CompletableFuture::join);

            long endTime = System.currentTimeMillis();
            long executionTime = endTime - startTime;

            // 计算效率（理想时间 vs 实际时间）
            long idealTime = 100; // 假设理想情况下100ms
            double efficiency = Math.min(1.0, (double) idealTime / executionTime);

            log.info("任务 '{}' 并行执行完成，耗时 {}ms，效率 {:.1f}%",
                task.getName(), executionTime, efficiency * 100);

            return efficiency;
        }

        private boolean shouldAdjustParallelism(double efficiency) {
            return efficiency < 0.7 || efficiency > 0.95; // 效率太低或过高都调整
        }

        private int adjustParallelism(int current, double efficiency) {
            if (efficiency < 0.7) {
                // 效率低，减少并行度
                return Math.max(1, current - 1);
            } else {
                // 效率高，增加并行度
                return Math.min(Runtime.getRuntime().availableProcessors() * 2, current + 1);
            }
        }
    }

    // 辅助类和枚举
    enum TaskType {
        CPU_INTENSIVE, IO_INTENSIVE, MEMORY_INTENSIVE, NETWORK_INTENSIVE, MIXED
    }

    static class TaskDefinition {
        private final String name;
        private final TaskType type;

        public TaskDefinition(String name, TaskType type) {
            this.name = name;
            this.type = type;
        }

        public String getName() { return name; }
        public TaskType getType() { return type; }
    }

    static class ParallelExecutionResult {
        private final int totalTasks;
        private final int parallelism;
        private final long executionTime;
        private final double speedupRatio;
        private final Map<String, TaskResult> taskResults;

        public ParallelExecutionResult(int totalTasks, int parallelism, long executionTime,
                                     double speedupRatio, Map<String, TaskResult> taskResults) {
            this.totalTasks = totalTasks;
            this.parallelism = parallelism;
            this.executionTime = executionTime;
            this.speedupRatio = speedupRatio;
            this.taskResults = taskResults;
        }

        public int getTotalTasks() { return totalTasks; }
        public int getParallelism() { return parallelism; }
        public long getExecutionTime() { return executionTime; }
        public double getSpeedupRatio() { return speedupRatio; }
        public Map<String, TaskResult> getTaskResults() { return taskResults; }
    }

    static class TaskResult {
        private final String task;
        private final boolean success;
        private final long executionTime;

        public TaskResult(String task, boolean success, long executionTime) {
            this.task = task;
            this.success = success;
            this.executionTime = executionTime;
        }

        public String getTask() { return task; }
        public boolean getSuccess() { return success; }
        public long getExecutionTime() { return executionTime; }
    }

    static class PipelineStage {
        private final String name;
        private final int parallelism;

        public PipelineStage(String name, int parallelism) {
            this.name = name;
            this.parallelism = parallelism;
        }

        public String getName() { return name; }
        public int getParallelism() { return parallelism; }
    }

    static class PipelineResult {
        private final int processedItems;
        private final int pipelineStages;
        private final long totalTime;
        private final long averageItemTime;
        private final Map<String, StageMetrics> stageMetrics;

        public PipelineResult(int processedItems, int pipelineStages, long totalTime,
                            long averageItemTime, Map<String, StageMetrics> stageMetrics) {
            this.processedItems = processedItems;
            this.pipelineStages = pipelineStages;
            this.totalTime = totalTime;
            this.averageItemTime = averageItemTime;
            this.stageMetrics = stageMetrics;
        }

        public int getProcessedItems() { return processedItems; }
        public int getPipelineStages() { return pipelineStages; }
        public long getTotalTime() { return totalTime; }
        public long getAverageItemTime() { return averageItemTime; }
        public Map<String, StageMetrics> getStageMetrics() { return stageMetrics; }
    }

    static class StageMetrics {
        private final int parallelism;
        private final double throughput;
        private final double utilization;

        public StageMetrics(int parallelism, double throughput, double utilization) {
            this.parallelism = parallelism;
            this.throughput = throughput;
            this.utilization = utilization;
        }

        public int getParallelism() { return parallelism; }
        public double getThroughput() { return throughput; }
        public double getUtilization() { return utilization; }
    }

    static class AdaptiveResult {
        private final int totalTasks;
        private final double averageParallelism;
        private final int parallelismAdjustments;
        private final double resourceUtilization;
        private final Map<TaskType, TaskTypeMetrics> taskTypeMetrics;

        public AdaptiveResult(int totalTasks, double averageParallelism, int parallelismAdjustments,
                            double resourceUtilization, Map<TaskType, TaskTypeMetrics> taskTypeMetrics) {
            this.totalTasks = totalTasks;
            this.averageParallelism = averageParallelism;
            this.parallelismAdjustments = parallelismAdjustments;
            this.resourceUtilization = resourceUtilization;
            this.taskTypeMetrics = taskTypeMetrics;
        }

        public int getTotalTasks() { return totalTasks; }
        public double getAverageParallelism() { return averageParallelism; }
        public int getParallelismAdjustments() { return parallelismAdjustments; }
        public double getResourceUtilization() { return resourceUtilization; }
        public Map<TaskType, TaskTypeMetrics> getTaskTypeMetrics() { return taskTypeMetrics; }
    }

    static class TaskTypeMetrics {
        private int totalExecutions = 0;
        private double totalEfficiency = 0.0;
        private int recommendedParallelism;

        public void addExecution(int parallelism, double efficiency) {
            this.recommendedParallelism = parallelism;
            this.totalExecutions++;
            this.totalEfficiency += efficiency;
        }

        public int getRecommendedParallelism() { return recommendedParallelism; }
        public double getActualEfficiency() {
            return totalExecutions > 0 ? totalEfficiency / totalExecutions : 0.0;
        }
    }
}
