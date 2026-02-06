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
 * 主管代理模式多Agent示例
 * 演示AgentScope中基于主管代理的多Agent协作模式
 */
@Component
@Profile("supervisor-multi-agent-example")
@RequiredArgsConstructor
public class SupervisorMultiAgentExample implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(SupervisorMultiAgentExample.class);

    private final AgentFactory agentFactory;

    @Override
    public void run(String... args) {
        log.info("=== AgentScope 主管代理模式多Agent示例 ===");

        try {
            // 示例1: 基础主管代理协作
            demonstrateBasicSupervisorCollaboration();

            // 示例2: 动态任务分配
            demonstrateDynamicTaskAssignment();

            // 示例3: 协作结果整合
            demonstrateCollaborativeResultAggregation();

            log.info("=== 主管代理模式多Agent示例执行完成 ===");

        } catch (Exception e) {
            log.error("主管代理模式示例执行失败", e);
        }

        System.exit(0);
    }

    /**
     * 示例1: 基础主管代理协作
     * 演示主管代理如何协调多个专业Agent完成复杂任务
     */
    private void demonstrateBasicSupervisorCollaboration() {
        log.info("--- 基础主管代理协作示例 ---");

        SupervisorCoordinator coordinator = new SupervisorCoordinator(agentFactory);

        // 定义复杂任务
        String complexTask = "开发一个完整的在线购物系统：包括用户管理、商品管理、订单处理、支付集成";

        // 执行主管协调的协作
        SupervisorResult result = coordinator.coordinateComplexTask(complexTask);

        log.info("主管代理协作结果:");
        log.info("  参与Agent数: {}", result.getParticipatingAgents());
        log.info("  执行任务数: {}", result.getExecutedTasks());
        log.info("  协作耗时: {}ms", result.getCollaborationTime());
        log.info("  协调效率: {:.2f}%", result.getCoordinationEfficiency() * 100);

        // 显示各Agent的贡献
        log.info("各Agent贡献详情:");
        result.getAgentContributions().forEach((agent, contribution) ->
            log.info("  {}: 处理了 {} 个任务，贡献度 {:.2f}%",
                agent, contribution.getTasksHandled(), contribution.getContributionRatio() * 100));

        log.info("✅ 基础主管代理协作完成");
    }

    /**
     * 示例2: 动态任务分配
     * 演示主管代理根据Agent能力和当前负载动态分配任务
     */
    private void demonstrateDynamicTaskAssignment() {
        log.info("--- 动态任务分配示例 ---");

        DynamicTaskAllocator allocator = new DynamicTaskAllocator(agentFactory);

        // 定义多类型任务
        List<Task> tasks = List.of(
            new Task("用户界面设计", TaskType.UI_DESIGN),
            new Task("后端API开发", TaskType.BACKEND_DEV),
            new Task("数据库设计", TaskType.DATABASE_DESIGN),
            new Task("测试用例编写", TaskType.TESTING),
            new Task("部署配置", TaskType.DEPLOYMENT),
            new Task("文档编写", TaskType.DOCUMENTATION)
        );

        // 执行动态任务分配
        AllocationResult result = allocator.allocateTasksDynamically(tasks);

        log.info("动态任务分配结果:");
        log.info("  总任务数: {}", result.getTotalTasks());
        log.info("  分配决策数: {}", result.getAllocationDecisions());
        log.info("  负载均衡度: {:.2f}%", result.getLoadBalanceRatio() * 100);

        // 显示分配详情
        log.info("任务分配详情:");
        result.getTaskAllocations().forEach((task, agent) ->
            log.info("  任务 '{}' 分配给 {}", task.getName(), agent));

        // 显示Agent负载情况
        log.info("Agent负载情况:");
        result.getAgentLoads().forEach((agent, load) ->
            log.info("  {}: 任务数={}, 负载率={:.1f}%",
                agent, load.getTaskCount(), load.getLoadRatio() * 100));

        log.info("✅ 动态任务分配完成");
    }

    /**
     * 示例3: 协作结果整合
     * 演示如何整合多个Agent的执行结果，形成完整的解决方案
     */
    private void demonstrateCollaborativeResultAggregation() {
        log.info("--- 协作结果整合示例 ---");

        ResultAggregator aggregator = new ResultAggregator(agentFactory);

        // 定义需要协作完成的综合任务
        String comprehensiveTask = "进行产品发布准备：包括代码审查、性能测试、安全检查、文档更新、用户培训";

        // 执行协作结果整合
        AggregationResult result = aggregator.aggregateCollaborativeResults(comprehensiveTask);

        log.info("协作结果整合结果:");
        log.info("  结果组件数: {}", result.getResultComponents());
        log.info("  整合质量评分: {:.2f}/5.0", result.getIntegrationQuality());
        log.info("  冲突解决次数: {}", result.getConflictResolutions());
        log.info("  最终输出完整性: {:.2f}%", result.getOutputCompleteness() * 100);

        // 显示整合过程
        log.info("整合过程详情:");
        result.getIntegrationSteps().forEach(step ->
            log.info("  {}: {}", step.getStepName(), step.getDescription()));

        // 显示最终整合结果
        log.info("最终整合结果预览:");
        log.info("  {}", result.getFinalResult().substring(0, 200) + "...");

        log.info("✅ 协作结果整合完成");
    }

    /**
     * 主管协调器
     */
    static class SupervisorCoordinator {

        private final AgentFactory agentFactory;
        private final Map<String, ReActAgent> workerAgents;

        public SupervisorCoordinator(AgentFactory agentFactory) {
            this.agentFactory = agentFactory;
            this.workerAgents = createWorkerAgents();
        }

        private Map<String, ReActAgent> createWorkerAgents() {
            return Map.of(
                "architect", agentFactory.createSpecialistAgent("系统架构师"),
                "developer", agentFactory.createSpecialistAgent("开发工程师"),
                "tester", agentFactory.createSpecialistAgent("测试工程师"),
                "dba", agentFactory.createSpecialistAgent("数据库管理员"),
                "devops", agentFactory.createSpecialistAgent("运维工程师")
            );
        }

        public SupervisorResult coordinateComplexTask(String task) {
            long startTime = System.currentTimeMillis();

            // 创建主管Agent
            ReActAgent supervisor = agentFactory.createBasicAssistant();

            // 第一步：任务分析和分解
            Msg analysisRequest = Msg.builder()
                .name("user")
                .role(MsgRole.USER)
                .content(List.of(TextBlock.builder()
                    .text("作为项目主管，请分析以下复杂任务并制定执行计划：\n" + task +
                          "\n\n请明确指出需要哪些专业角色参与，每个角色负责什么任务。")
                    .build()))
                .build();

            Msg analysisResult = supervisor.call(analysisRequest).block();
            log.info("任务分析结果:\n{}", analysisResult.getTextContent());

            // 解析任务分配
            Map<String, List<String>> taskAssignments = parseTaskAssignments(analysisResult.getTextContent());

            // 第二步：并行执行分配的任务
            Map<String, CompletableFuture<List<String>>> executionFutures = new java.util.HashMap<>();

            for (Map.Entry<String, List<String>> assignment : taskAssignments.entrySet()) {
                String agentName = assignment.getKey();
                List<String> tasks = assignment.getValue();

                executionFutures.put(agentName, CompletableFuture.supplyAsync(() ->
                    executeAgentTasks(agentName, tasks)));
            }

            // 等待所有执行完成
            Map<String, List<String>> executionResults = executionFutures.entrySet().stream()
                .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    entry -> entry.getValue().join()
                ));

            // 第三步：结果汇总和整合
            Msg summaryRequest = Msg.builder()
                .name("supervisor")
                .role(MsgRole.SYSTEM)
                .content(List.of(TextBlock.builder()
                    .text("请整合以下各专业角色的执行结果，形成完整的解决方案：\n" +
                        formatExecutionResults(executionResults))
                    .build()))
                .build();

            Msg finalResult = supervisor.call(summaryRequest).block();

            long endTime = System.currentTimeMillis();

            // 计算统计信息
            int totalTasks = taskAssignments.values().stream().mapToInt(List::size).sum();
            int participatingAgents = taskAssignments.size();
            double efficiency = calculateEfficiency(executionResults, totalTasks);

            Map<String, AgentContribution> contributions = calculateContributions(taskAssignments, executionResults);

            return new SupervisorResult(
                participatingAgents,
                totalTasks,
                endTime - startTime,
                efficiency,
                contributions
            );
        }

        private Map<String, List<String>> parseTaskAssignments(String analysisText) {
            // 简单的任务分配解析
            Map<String, List<String>> assignments = new java.util.HashMap<>();

            // 模拟任务分配（实际应该解析analysisText）
            assignments.put("architect", List.of("设计系统架构", "制定技术方案"));
            assignments.put("developer", List.of("实现用户管理模块", "实现商品管理模块", "实现订单处理模块"));
            assignments.put("dba", List.of("设计数据库结构", "优化查询性能"));
            assignments.put("tester", List.of("编写测试用例", "执行集成测试"));
            assignments.put("devops", List.of("配置CI/CD管道", "设置生产环境"));

            return assignments;
        }

        private List<String> executeAgentTasks(String agentName, List<String> tasks) {
            ReActAgent agent = workerAgents.get(agentName);
            List<String> results = new java.util.ArrayList<>();

            for (String task : tasks) {
                try {
                    Msg taskRequest = Msg.builder()
                        .name("supervisor")
                        .role(MsgRole.SYSTEM)
                        .content(List.of(TextBlock.builder()
                            .text("请执行以下任务：\n" + task)
                            .build()))
                        .build();

                    Msg taskResult = agent.call(taskRequest).block();
                    results.add(taskResult.getTextContent());

                    log.info("Agent '{}' 完成任务: {}", agentName, task);

                } catch (Exception e) {
                    log.warn("Agent '{}' 执行任务 '{}' 失败", agentName, task, e);
                    results.add("任务执行失败: " + e.getMessage());
                }
            }

            return results;
        }

        private String formatExecutionResults(Map<String, List<String>> results) {
            StringBuilder formatted = new StringBuilder();
            results.forEach((agent, agentResults) -> {
                formatted.append("\n").append(agent).append(" 的执行结果:\n");
                for (int i = 0; i < agentResults.size(); i++) {
                    formatted.append("  ").append(i + 1).append(". ")
                            .append(agentResults.get(i).substring(0, Math.min(100, agentResults.get(i).length())))
                            .append("...\n");
                }
            });
            return formatted.toString();
        }

        private double calculateEfficiency(Map<String, List<String>> results, int totalTasks) {
            int successfulTasks = results.values().stream()
                .mapToInt(agentResults -> (int) agentResults.stream()
                    .filter(result -> !result.contains("失败"))
                    .count())
                .sum();

            return totalTasks > 0 ? (double) successfulTasks / totalTasks : 0.0;
        }

        private Map<String, AgentContribution> calculateContributions(
                Map<String, List<String>> assignments, Map<String, List<String>> results) {

            return assignments.entrySet().stream()
                .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    entry -> {
                        String agent = entry.getKey();
                        int assignedTasks = entry.getValue().size();
                        List<String> agentResults = results.get(agent);
                        int successfulTasks = agentResults != null ?
                            (int) agentResults.stream().filter(r -> !r.contains("失败")).count() : 0;

                        double contributionRatio = assignedTasks > 0 ?
                            (double) successfulTasks / assignedTasks : 0.0;

                        return new AgentContribution(assignedTasks, successfulTasks, contributionRatio);
                    }
                ));
        }
    }

    /**
     * 动态任务分配器
     */
    static class DynamicTaskAllocator {

        private final AgentFactory agentFactory;
        private final Map<String, AgentCapability> agentCapabilities;

        public DynamicTaskAllocator(AgentFactory agentFactory) {
            this.agentFactory = agentFactory;
            this.agentCapabilities = initializeAgentCapabilities();
        }

        private Map<String, AgentCapability> initializeAgentCapabilities() {
            Map<String, AgentCapability> capabilities = new java.util.HashMap<>();
            capabilities.put("ui-specialist", new AgentCapability(List.of(TaskType.UI_DESIGN), 2));
            capabilities.put("backend-dev", new AgentCapability(List.of(TaskType.BACKEND_DEV), 3));
            capabilities.put("database-expert", new AgentCapability(List.of(TaskType.DATABASE_DESIGN), 1));
            capabilities.put("qa-engineer", new AgentCapability(List.of(TaskType.TESTING), 2));
            capabilities.put("devops-engineer", new AgentCapability(List.of(TaskType.DEPLOYMENT), 1));
            capabilities.put("technical-writer", new AgentCapability(List.of(TaskType.DOCUMENTATION), 2));
            return capabilities;
        }

        public AllocationResult allocateTasksDynamically(List<Task> tasks) {
            ReActAgent allocator = agentFactory.createTaskAllocatorAgent();

            Map<String, Integer> currentLoads = new java.util.HashMap<>();
            Map<String, String> taskAllocations = new java.util.HashMap<>();
            int decisionCount = 0;

            for (Task task : tasks) {
                // 基于当前负载和Agent能力进行分配决策
                Msg allocationRequest = Msg.builder()
                    .name("system")
                    .role(MsgRole.SYSTEM)
                    .content(List.of(TextBlock.builder()
                        .text(String.format("请为任务 '%s' (类型: %s) 选择最合适的Agent。\n" +
                            "可用Agent及当前负载: %s\n\n请选择一个Agent来执行这个任务。",
                            task.getName(), task.getType(), formatCurrentLoads(currentLoads)))
                        .build()))
                    .build();

                Msg allocationDecision = allocator.call(allocationRequest).block();
                String selectedAgent = parseAgentSelection(allocationDecision.getTextContent());

                // 更新分配结果
                taskAllocations.put(task.getName(), selectedAgent);
                currentLoads.merge(selectedAgent, 1, Integer::sum);
                decisionCount++;

                log.info("任务 '{}' 分配给 Agent '{}'", task.getName(), selectedAgent);
            }

            // 计算负载均衡度
            double loadBalanceRatio = calculateLoadBalance(currentLoads);

            Map<String, AgentLoad> agentLoads = currentLoads.entrySet().stream()
                .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    entry -> {
                        int taskCount = entry.getValue();
                        AgentCapability capability = agentCapabilities.get(entry.getKey());
                        double loadRatio = capability != null && capability.getMaxConcurrentTasks() > 0 ?
                            (double) taskCount / capability.getMaxConcurrentTasks() : 1.0;
                        return new AgentLoad(taskCount, loadRatio);
                    }
                ));

            return new AllocationResult(
                tasks.size(),
                decisionCount,
                loadBalanceRatio,
                taskAllocations,
                agentLoads
            );
        }

        private String formatCurrentLoads(Map<String, Integer> loads) {
            return agentCapabilities.keySet().stream()
                .map(agent -> String.format("%s(当前负载:%d)", agent, loads.getOrDefault(agent, 0)))
                .collect(Collectors.joining(", "));
        }

        private String parseAgentSelection(String decisionText) {
            // 简单的决策解析
            for (String agent : agentCapabilities.keySet()) {
                if (decisionText.contains(agent)) {
                    return agent;
                }
            }
            // 默认选择第一个
            return agentCapabilities.keySet().iterator().next();
        }

        private double calculateLoadBalance(Map<String, Integer> loads) {
            if (loads.isEmpty()) return 1.0;

            double avgLoad = loads.values().stream().mapToInt(Integer::intValue).average().orElse(0.0);
            if (avgLoad == 0) return 1.0;

            double variance = loads.values().stream()
                .mapToDouble(load -> Math.pow(load - avgLoad, 2))
                .average()
                .orElse(0.0);

            // 标准化方差作为不均衡度，值越小越均衡
            double imbalance = Math.sqrt(variance) / avgLoad;
            return Math.max(0.0, 1.0 - imbalance); // 转换为均衡度
        }
    }

    /**
     * 结果聚合器
     */
    static class ResultAggregator {

        private final AgentFactory agentFactory;

        public ResultAggregator(AgentFactory agentFactory) {
            this.agentFactory = agentFactory;
        }

        public AggregationResult aggregateCollaborativeResults(String comprehensiveTask) {
            long startTime = System.currentTimeMillis();

            // 创建多个专业Agent
            Map<String, ReActAgent> specialists = Map.of(
                "reviewer", agentFactory.createSpecialistAgent("代码审查员"),
                "performance-tester", agentFactory.createSpecialistAgent("性能测试工程师"),
                "security-auditor", agentFactory.createSpecialistAgent("安全审计员"),
                "technical-writer", agentFactory.createSpecialistAgent("技术文档工程师"),
                "trainer", agentFactory.createSpecialistAgent("培训师")
            );

            // 定义各Agent的任务
            Map<String, String> specialistTasks = Map.of(
                "reviewer", "进行代码审查，确保代码质量和规范",
                "performance-tester", "执行性能测试，分析系统瓶颈",
                "security-auditor", "进行安全检查，识别潜在风险",
                "technical-writer", "编写技术文档和用户手册",
                "trainer", "准备用户培训材料和培训计划"
            );

            // 并行执行各Agent的任务
            Map<String, CompletableFuture<String>> resultFutures = specialistTasks.entrySet().stream()
                .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    entry -> CompletableFuture.supplyAsync(() ->
                        executeSpecialistTask(specialists.get(entry.getKey()), entry.getValue()))
                ));

            // 收集所有结果
            Map<String, String> specialistResults = resultFutures.entrySet().stream()
                .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    entry -> entry.getValue().join()
                ));

            // 使用整合Agent整合结果
            ReActAgent integrator = agentFactory.createResultIntegratorAgent();

            Msg integrationRequest = Msg.builder()
                .name("system")
                .role(MsgRole.SYSTEM)
                .content(List.of(TextBlock.builder()
                    .text("请整合以下各专家的分析结果，形成完整的" + comprehensiveTask + "报告：\n" +
                        formatSpecialistResults(specialistResults) +
                        "\n\n请确保报告结构完整，包含所有重要方面，并解决任何冲突或不一致的地方。")
                    .build()))
                .build();

            Msg integratedResult = integrator.call(integrationRequest).block();

            long endTime = System.currentTimeMillis();

            // 分析整合质量
            double integrationQuality = analyzeIntegrationQuality(integratedResult.getTextContent());
            int conflictResolutions = countConflictResolutions(integratedResult.getTextContent());
            double outputCompleteness = calculateOutputCompleteness(integratedResult.getTextContent());

            List<IntegrationStep> integrationSteps = List.of(
                new IntegrationStep("结果收集", "收集各专家的分析结果"),
                new IntegrationStep("冲突识别", "识别结果间的冲突和不一致"),
                new IntegrationStep("内容整合", "整合相关内容，避免重复"),
                new IntegrationStep("结构优化", "优化报告结构和逻辑 flow"),
                new IntegrationStep("质量检查", "检查整合结果的完整性和准确性")
            );

            return new AggregationResult(
                specialistResults.size(),
                integrationQuality,
                conflictResolutions,
                outputCompleteness,
                integrationSteps,
                integratedResult.getTextContent()
            );
        }

        private String executeSpecialistTask(ReActAgent specialist, String task) {
            try {
                Msg taskRequest = Msg.builder()
                    .name("coordinator")
                    .role(MsgRole.SYSTEM)
                    .content(List.of(TextBlock.builder()
                        .text("请执行以下专业任务：\n" + task)
                        .build()))
                    .build();

                Msg result = specialist.call(taskRequest).block();
                return result.getTextContent();

            } catch (Exception e) {
                log.warn("专家任务执行失败: {}", task, e);
                return "任务执行失败: " + e.getMessage();
            }
        }

        private String formatSpecialistResults(Map<String, String> results) {
            StringBuilder formatted = new StringBuilder();
            results.forEach((specialist, result) -> {
                formatted.append("\n").append(specialist.toUpperCase()).append(" 报告:\n");
                formatted.append(result.substring(0, Math.min(300, result.length())));
                if (result.length() > 300) formatted.append("...");
                formatted.append("\n");
            });
            return formatted.toString();
        }

        private double analyzeIntegrationQuality(String integratedResult) {
            // 简单的质量分析
            double score = 3.0; // 基础分数

            if (integratedResult.contains("总结") || integratedResult.contains("结论")) score += 0.5;
            if (integratedResult.contains("建议") || integratedResult.contains("推荐")) score += 0.5;
            if (integratedResult.contains("风险") || integratedResult.contains("注意事项")) score += 0.5;
            if (integratedResult.length() > 1000) score += 0.5; // 内容丰富

            return Math.min(5.0, score);
        }

        private int countConflictResolutions(String integratedResult) {
            // 简单的冲突解决计数
            String[] conflictKeywords = {"但是", "然而", "不过", "综合考虑", "平衡"};
            int count = 0;
            for (String keyword : conflictKeywords) {
                count += integratedResult.split(keyword, -1).length - 1;
            }
            return count;
        }

        private double calculateOutputCompleteness(String integratedResult) {
            // 检查输出完整性
            String[] requiredSections = {"介绍", "分析", "结论", "建议"};
            int foundSections = 0;

            for (String section : requiredSections) {
                if (integratedResult.toLowerCase().contains(section.toLowerCase())) {
                    foundSections++;
                }
            }

            return (double) foundSections / requiredSections.length;
        }
    }

    // 辅助类和枚举
    enum TaskType {
        UI_DESIGN, BACKEND_DEV, DATABASE_DESIGN, TESTING, DEPLOYMENT, DOCUMENTATION
    }

    static class Task {
        private final String name;
        private final TaskType type;

        public Task(String name, TaskType type) {
            this.name = name;
            this.type = type;
        }

        public String getName() { return name; }
        public TaskType getType() { return type; }
    }

    static class SupervisorResult {
        private final int participatingAgents;
        private final int executedTasks;
        private final long collaborationTime;
        private final double coordinationEfficiency;
        private final Map<String, AgentContribution> agentContributions;

        public SupervisorResult(int participatingAgents, int executedTasks, long collaborationTime,
                              double coordinationEfficiency, Map<String, AgentContribution> agentContributions) {
            this.participatingAgents = participatingAgents;
            this.executedTasks = executedTasks;
            this.collaborationTime = collaborationTime;
            this.coordinationEfficiency = coordinationEfficiency;
            this.agentContributions = agentContributions;
        }

        public int getParticipatingAgents() { return participatingAgents; }
        public int getExecutedTasks() { return executedTasks; }
        public long getCollaborationTime() { return collaborationTime; }
        public double getCoordinationEfficiency() { return coordinationEfficiency; }
        public Map<String, AgentContribution> getAgentContributions() { return agentContributions; }
    }

    static class AgentContribution {
        private final int tasksAssigned;
        private final int tasksHandled;
        private final double contributionRatio;

        public AgentContribution(int tasksAssigned, int tasksHandled, double contributionRatio) {
            this.tasksAssigned = tasksAssigned;
            this.tasksHandled = tasksHandled;
            this.contributionRatio = contributionRatio;
        }

        public int getTasksHandled() { return tasksHandled; }
        public double getContributionRatio() { return contributionRatio; }
    }

    static class AllocationResult {
        private final int totalTasks;
        private final int allocationDecisions;
        private final double loadBalanceRatio;
        private final Map<String, String> taskAllocations;
        private final Map<String, AgentLoad> agentLoads;

        public AllocationResult(int totalTasks, int allocationDecisions, double loadBalanceRatio,
                              Map<String, String> taskAllocations, Map<String, AgentLoad> agentLoads) {
            this.totalTasks = totalTasks;
            this.allocationDecisions = allocationDecisions;
            this.loadBalanceRatio = loadBalanceRatio;
            this.taskAllocations = taskAllocations;
            this.agentLoads = agentLoads;
        }

        public int getTotalTasks() { return totalTasks; }
        public int getAllocationDecisions() { return allocationDecisions; }
        public double getLoadBalanceRatio() { return loadBalanceRatio; }
        public Map<String, String> getTaskAllocations() { return taskAllocations; }
        public Map<String, AgentLoad> getAgentLoads() { return agentLoads; }
    }

    static class AgentCapability {
        private final List<TaskType> supportedTypes;
        private final int maxConcurrentTasks;

        public AgentCapability(List<TaskType> supportedTypes, int maxConcurrentTasks) {
            this.supportedTypes = supportedTypes;
            this.maxConcurrentTasks = maxConcurrentTasks;
        }

        public int getMaxConcurrentTasks() { return maxConcurrentTasks; }
    }

    static class AgentLoad {
        private final int taskCount;
        private final double loadRatio;

        public AgentLoad(int taskCount, double loadRatio) {
            this.taskCount = taskCount;
            this.loadRatio = loadRatio;
        }

        public int getTaskCount() { return taskCount; }
        public double getLoadRatio() { return loadRatio; }
    }

    static class AggregationResult {
        private final int resultComponents;
        private final double integrationQuality;
        private final int conflictResolutions;
        private final double outputCompleteness;
        private final List<IntegrationStep> integrationSteps;
        private final String finalResult;

        public AggregationResult(int resultComponents, double integrationQuality, int conflictResolutions,
                               double outputCompleteness, List<IntegrationStep> integrationSteps, String finalResult) {
            this.resultComponents = resultComponents;
            this.integrationQuality = integrationQuality;
            this.conflictResolutions = conflictResolutions;
            this.outputCompleteness = outputCompleteness;
            this.integrationSteps = integrationSteps;
            this.finalResult = finalResult;
        }

        public int getResultComponents() { return resultComponents; }
        public double getIntegrationQuality() { return integrationQuality; }
        public int getConflictResolutions() { return conflictResolutions; }
        public double getOutputCompleteness() { return outputCompleteness; }
        public List<IntegrationStep> getIntegrationSteps() { return integrationSteps; }
        public String getFinalResult() { return finalResult; }
    }

    static class IntegrationStep {
        private final String stepName;
        private final String description;

        public IntegrationStep(String stepName, String description) {
            this.stepName = stepName;
            this.description = description;
        }

        public String getStepName() { return stepName; }
        public String getDescription() { return description; }
    }
}
