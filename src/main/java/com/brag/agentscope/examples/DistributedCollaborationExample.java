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
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 分布式协作多Agent示例
 * 演示AgentScope中分布式多Agent协作模式
 */
@Component
@Profile("distributed-collaboration-example")
@RequiredArgsConstructor
public class DistributedCollaborationExample implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DistributedCollaborationExample.class);

    private final AgentFactory agentFactory;

    @Override
    public void run(String... args) {
        log.info("=== AgentScope 分布式协作多Agent示例 ===");

        try {
            // 示例1: 分布式任务执行
            demonstrateDistributedTaskExecution();

            // 示例2: 协作状态同步
            demonstrateCollaborativeStateSync();

            // 示例3: 分布式冲突解决
            demonstrateDistributedConflictResolution();

            log.info("=== 分布式协作多Agent示例执行完成 ===");

        } catch (Exception e) {
            log.error("分布式协作示例执行失败", e);
        }

        System.exit(0);
    }

    /**
     * 示例1: 分布式任务执行
     * 演示多个Agent在分布式环境中协同执行复杂任务
     */
    private void demonstrateDistributedTaskExecution() {
        log.info("--- 分布式任务执行示例 ---");

        DistributedTaskExecutor executor = new DistributedTaskExecutor(agentFactory);

        // 定义需要分布式协作的任务
        String distributedTask = "构建完整的微服务架构：包括服务设计、API网关、配置中心、服务注册发现、监控告警";

        // 执行分布式任务
        DistributedResult result = executor.executeDistributedTask(distributedTask);

        log.info("分布式任务执行结果:");
        log.info("  参与节点数: {}", result.getParticipatingNodes());
        log.info("  执行任务数: {}", result.getExecutedTasks());
        log.info("  网络通信次数: {}", result.getNetworkCommunications());
        log.info("  总执行时间: {}ms", result.getTotalExecutionTime());
        log.info("  分布式效率: {:.2f}%", result.getDistributedEfficiency() * 100);

        // 显示节点贡献
        log.info("各节点贡献详情:");
        result.getNodeContributions().forEach((node, contribution) ->
            log.info("  {}: 完成 {} 个任务，通信 {} 次，效率 {:.2f}%",
                node, contribution.getTasksCompleted(),
                contribution.getCommunications(), contribution.getEfficiency() * 100));

        log.info("✅ 分布式任务执行完成");
    }

    /**
     * 示例2: 协作状态同步
     * 演示多Agent间的状态同步和协调机制
     */
    private void demonstrateCollaborativeStateSync() {
        log.info("--- 协作状态同步示例 ---");

        StateSynchronizationManager syncManager = new StateSynchronizationManager(agentFactory);

        // 定义需要状态同步的协作场景
        List<String> collaborationAgents = List.of("architect", "developer", "tester", "devops");

        // 执行状态同步协作
        SyncResult result = syncManager.executeStateSynchronizedCollaboration(collaborationAgents);

        log.info("状态同步协作结果:");
        log.info("  同步操作数: {}", result.getSyncOperations());
        log.info("  状态一致性: {:.2f}%", result.getStateConsistency() * 100);
        log.info("  同步延迟: {}ms", result.getSyncLatency());
        log.info("  冲突解决次数: {}", result.getConflictResolutions());

        // 显示同步历史
        log.info("状态同步历史:");
        result.getSyncHistory().forEach(sync ->
            log.info("  {} -> {}: {} ({})", sync.getFromAgent(), sync.getToAgent(),
                sync.getOperation(), sync.getTimestamp()));

        log.info("✅ 协作状态同步完成");
    }

    /**
     * 示例3: 分布式冲突解决
     * 演示分布式环境中Agent间的冲突检测和解决
     */
    private void demonstrateDistributedConflictResolution() {
        log.info("--- 分布式冲突解决示例 ---");

        ConflictResolutionManager conflictManager = new ConflictResolutionManager(agentFactory);

        // 定义可能产生冲突的协作场景
        String conflictScenario = "多个开发团队同时修改同一个微服务：API变更、数据库迁移、配置更新";

        // 执行冲突解决流程
        ConflictResult result = conflictManager.resolveDistributedConflicts(conflictScenario);

        log.info("分布式冲突解决结果:");
        log.info("  检测到冲突数: {}", result.getDetectedConflicts());
        log.info("  成功解决冲突数: {}", result.getResolvedConflicts());
        log.info("  解决准确率: {:.2f}%", result.getResolutionAccuracy() * 100);
        log.info("  平均解决时间: {}ms", result.getAverageResolutionTime());

        // 显示冲突详情
        log.info("冲突解决详情:");
        result.getConflictDetails().forEach(conflict ->
            log.info("  冲突类型: {}, 涉及Agent: {}, 解决策略: {}, 结果: {}",
                conflict.getType(), conflict.getInvolvedAgents(),
                conflict.getResolutionStrategy(), conflict.getOutcome()));

        log.info("✅ 分布式冲突解决完成");
    }

    /**
     * 分布式任务执行器
     */
    static class DistributedTaskExecutor {

        private final AgentFactory agentFactory;
        private final Map<String, ReActAgent> nodeAgents;

        public DistributedTaskExecutor(AgentFactory agentFactory) {
            this.agentFactory = agentFactory;
            this.nodeAgents = createNodeAgents();
        }

        private Map<String, ReActAgent> createNodeAgents() {
            return Map.of(
                "node-1", agentFactory.createSpecialistAgent("架构设计师"),
                "node-2", agentFactory.createSpecialistAgent("后端开发者"),
                "node-3", agentFactory.createSpecialistAgent("前端开发者"),
                "node-4", agentFactory.createSpecialistAgent("测试工程师"),
                "node-5", agentFactory.createSpecialistAgent("运维工程师")
            );
        }

        public DistributedResult executeDistributedTask(String task) {
            long startTime = System.currentTimeMillis();

            // 任务分解
            Map<String, List<String>> taskDistribution = decomposeAndDistributeTask(task);

            // 并行执行分布式任务
            Map<String, CompletableFuture<NodeResult>> executionFutures = taskDistribution.entrySet().stream()
                .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    entry -> CompletableFuture.supplyAsync(() ->
                        executeNodeTasks(entry.getKey(), entry.getValue()))
                ));

            // 收集执行结果
            Map<String, NodeResult> nodeResults = executionFutures.entrySet().stream()
                .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    entry -> entry.getValue().join()
                ));

            // 模拟网络通信
            int networkCommunications = simulateNetworkCommunications(nodeResults);

            long endTime = System.currentTimeMillis();

            // 计算统计信息
            int totalTasks = taskDistribution.values().stream().mapToInt(List::size).sum();
            double efficiency = calculateDistributedEfficiency(nodeResults, networkCommunications);

            Map<String, NodeContribution> contributions = calculateNodeContributions(nodeResults);

            return new DistributedResult(
                nodeResults.size(),
                totalTasks,
                networkCommunications,
                endTime - startTime,
                efficiency,
                contributions
            );
        }

        private Map<String, List<String>> decomposeAndDistributeTask(String task) {
            // 任务分解和分配逻辑
            return Map.of(
                "node-1", List.of("设计微服务架构", "定义服务边界"),
                "node-2", List.of("实现用户服务API", "实现订单服务API"),
                "node-3", List.of("开发管理后台界面", "开发用户前端界面"),
                "node-4", List.of("编写服务测试用例", "设置集成测试环境"),
                "node-5", List.of("配置Kubernetes集群", "设置监控告警系统")
            );
        }

        private NodeResult executeNodeTasks(String nodeId, List<String> tasks) {
            ReActAgent nodeAgent = nodeAgents.get(nodeId);
            List<String> results = new java.util.ArrayList<>();
            int communications = 0;

            for (String task : tasks) {
                try {
                    Msg taskRequest = Msg.builder()
                        .name("coordinator")
                        .role(MsgRole.SYSTEM)
                        .content(List.of(TextBlock.builder()
                            .text("请在分布式环境中执行任务：\n" + task +
                                  "\n\n注意：这是一个分布式协作环境，你可能需要与其他节点协调。")
                            .build()))
                        .build();

                    Msg taskResult = nodeAgent.call(taskRequest).block();
                    results.add(taskResult.getTextContent());

                    // 模拟与其他节点的通信
                    communications += (int) (Math.random() * 3) + 1;

                    log.info("节点 {} 完成任务: {}", nodeId, task);

                } catch (Exception e) {
                    log.warn("节点 {} 执行任务失败: {}", nodeId, task, e);
                    results.add("任务执行失败: " + e.getMessage());
                }
            }

            return new NodeResult(nodeId, results, communications);
        }

        private int simulateNetworkCommunications(Map<String, NodeResult> nodeResults) {
            return nodeResults.values().stream()
                .mapToInt(NodeResult::getCommunications)
                .sum();
        }

        private double calculateDistributedEfficiency(Map<String, NodeResult> nodeResults, int communications) {
            int successfulTasks = nodeResults.values().stream()
                .mapToInt(result -> (int) result.getResults().stream()
                    .filter(r -> !r.contains("失败"))
                    .count())
                .sum();

            int totalTasks = nodeResults.values().stream()
                .mapToInt(result -> result.getResults().size())
                .sum();

            // 考虑通信开销
            double communicationPenalty = Math.min(0.3, communications * 0.01);

            double baseEfficiency = totalTasks > 0 ? (double) successfulTasks / totalTasks : 0.0;

            return Math.max(0.0, baseEfficiency - communicationPenalty);
        }

        private Map<String, NodeContribution> calculateNodeContributions(Map<String, NodeResult> nodeResults) {
            return nodeResults.entrySet().stream()
                .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    entry -> {
                        NodeResult result = entry.getValue();
                        int tasksCompleted = (int) result.getResults().stream()
                            .filter(r -> !r.contains("失败"))
                            .count();
                        double efficiency = result.getResults().size() > 0 ?
                            (double) tasksCompleted / result.getResults().size() : 0.0;

                        return new NodeContribution(tasksCompleted, result.getCommunications(), efficiency);
                    }
                ));
        }
    }

    /**
     * 状态同步管理器
     */
    static class StateSynchronizationManager {

        private final AgentFactory agentFactory;
        private final Map<String, AgentState> agentStates;
        private final List<StateSyncOperation> syncHistory;

        public StateSynchronizationManager(AgentFactory agentFactory) {
            this.agentFactory = agentFactory;
            this.agentStates = new ConcurrentHashMap<>();
            this.syncHistory = new java.util.ArrayList<>();
        }

        public SyncResult executeStateSynchronizedCollaboration(List<String> agentNames) {
            // 初始化Agent状态
            agentNames.forEach(name -> {
                agentStates.put(name, new AgentState(name, "initialized"));
                syncHistory.add(new StateSyncOperation("coordinator", name, "initialize", System.currentTimeMillis()));
            });

            // 执行协作任务
            String collaborationTask = "协作开发一个新功能：需求分析、设计、实现、测试";

            Map<String, CompletableFuture<Void>> collaborationFutures = agentNames.stream()
                .collect(Collectors.toMap(
                    name -> name,
                    name -> CompletableFuture.runAsync(() ->
                        performAgentCollaboration(name, collaborationTask))
                ));

            // 等待所有协作完成
            collaborationFutures.values().forEach(CompletableFuture::join);

            // 计算同步统计
            long syncOperations = syncHistory.size();
            double consistency = calculateStateConsistency(agentStates);
            long avgLatency = calculateAverageSyncLatency(syncHistory);
            int conflictResolutions = (int) syncHistory.stream()
                .filter(op -> op.getOperation().contains("resolve"))
                .count();

            return new SyncResult(syncOperations, consistency, avgLatency, conflictResolutions, syncHistory);
        }

        private void performAgentCollaboration(String agentName, String task) {
            ReActAgent agent = agentFactory.createSpecialistAgent(agentName);

            try {
                // 更新状态为工作中
                updateAgentState(agentName, "working");

                Msg taskRequest = Msg.builder()
                    .name("coordinator")
                    .role(MsgRole.SYSTEM)
                    .content(List.of(TextBlock.builder()
                        .text("作为 " + agentName + " 参与协作任务：\n" + task +
                              "\n\n请及时与其他Agent同步状态和进展。")
                        .build()))
                    .build();

                Msg result = agent.call(taskRequest).block();

                // 更新状态为完成
                updateAgentState(agentName, "completed");

                // 广播状态更新
                broadcastStateUpdate(agentName, "completed");

                log.info("Agent {} 完成协作任务", agentName);

            } catch (Exception e) {
                // 更新状态为失败
                updateAgentState(agentName, "failed");
                broadcastStateUpdate(agentName, "failed");

                log.warn("Agent {} 协作失败", agentName, e);
            }
        }

        private void updateAgentState(String agentName, String newState) {
            AgentState state = agentStates.get(agentName);
            if (state != null) {
                state.setState(newState);
                state.setLastUpdated(System.currentTimeMillis());
            }
        }

        private void broadcastStateUpdate(String fromAgent, String newState) {
            agentStates.keySet().stream()
                .filter(agent -> !agent.equals(fromAgent))
                .forEach(toAgent -> {
                    syncHistory.add(new StateSyncOperation(fromAgent, toAgent,
                        "state-update:" + newState, System.currentTimeMillis()));
                });
        }

        private double calculateStateConsistency(Map<String, AgentState> states) {
            if (states.isEmpty()) return 1.0;

            // 计算状态差异度
            long currentTime = System.currentTimeMillis();
            double totalAge = states.values().stream()
                .mapToLong(state -> currentTime - state.getLastUpdated())
                .average()
                .orElse(0.0);

            // 年龄差异越小，一致性越高
            double maxAge = 30000.0; // 30秒
            return Math.max(0.0, 1.0 - (totalAge / maxAge));
        }

        private long calculateAverageSyncLatency(List<StateSyncOperation> operations) {
            if (operations.isEmpty()) return 0;

            return operations.stream()
                .mapToLong(op -> (long) (Math.random() * 100) + 10) // 模拟延迟
                .average()
                .orElse(0);
        }
    }

    /**
     * 冲突解决管理器
     */
    static class ConflictResolutionManager {

        private final AgentFactory agentFactory;

        public ConflictResolutionManager(AgentFactory agentFactory) {
            this.agentFactory = agentFactory;
        }

        public ConflictResult resolveDistributedConflicts(String scenario) {
            ReActAgent conflictResolver = agentFactory.createConflictResolverAgent();

            // 模拟冲突场景
            List<Conflict> simulatedConflicts = generateSimulatedConflicts(scenario);

            int detectedConflicts = simulatedConflicts.size();
            int resolvedConflicts = 0;
            long totalResolutionTime = 0;
            List<ConflictDetail> conflictDetails = new java.util.ArrayList<>();

            for (Conflict conflict : simulatedConflicts) {
                long startTime = System.currentTimeMillis();

                Msg resolutionRequest = Msg.builder()
                    .name("coordinator")
                    .role(MsgRole.SYSTEM)
                    .content(List.of(TextBlock.builder()
                        .text("请解决以下分布式冲突：\n" +
                            "冲突类型: " + conflict.getType() + "\n" +
                            "涉及Agent: " + String.join(", ", conflict.getInvolvedAgents()) + "\n" +
                            "冲突描述: " + conflict.getDescription() + "\n\n" +
                            "请提供具体的解决策略和建议。")
                        .build()))
                    .build();

                try {
                    Msg resolution = conflictResolver.call(resolutionRequest).block();
                    resolvedConflicts++;

                    long endTime = System.currentTimeMillis();
                    totalResolutionTime += (endTime - startTime);

                    conflictDetails.add(new ConflictDetail(
                        conflict.getType(),
                        conflict.getInvolvedAgents(),
                        "协商解决", // 简化的解决策略
                        "已解决"
                    ));

                    log.info("成功解决冲突: {}", conflict.getType());

                } catch (Exception e) {
                    long endTime = System.currentTimeMillis();
                    totalResolutionTime += (endTime - startTime);

                    conflictDetails.add(new ConflictDetail(
                        conflict.getType(),
                        conflict.getInvolvedAgents(),
                        "协商解决",
                        "解决失败"
                    ));

                    log.warn("冲突解决失败: {}", conflict.getType(), e);
                }
            }

            double resolutionAccuracy = detectedConflicts > 0 ?
                (double) resolvedConflicts / detectedConflicts : 0.0;
            long avgResolutionTime = resolvedConflicts > 0 ?
                totalResolutionTime / resolvedConflicts : 0;

            return new ConflictResult(detectedConflicts, resolvedConflicts,
                resolutionAccuracy, avgResolutionTime, conflictDetails);
        }

        private List<Conflict> generateSimulatedConflicts(String scenario) {
            // 基于场景生成模拟冲突
            return List.of(
                new Conflict("代码合并冲突", List.of("developer-1", "developer-2"),
                    "两个开发者同时修改了同一个文件"),
                new Conflict("API设计分歧", List.of("architect", "backend-dev"),
                    "对REST API设计有不同意见"),
                new Conflict("数据库结构变更", List.of("backend-dev", "dba"),
                    "对表结构修改存在分歧"),
                new Conflict("测试环境资源竞争", List.of("tester", "devops"),
                    "测试环境资源分配冲突")
            );
        }
    }

    // 辅助类
    static class DistributedResult {
        private final int participatingNodes;
        private final int executedTasks;
        private final int networkCommunications;
        private final long totalExecutionTime;
        private final double distributedEfficiency;
        private final Map<String, NodeContribution> nodeContributions;

        public DistributedResult(int participatingNodes, int executedTasks, int networkCommunications,
                               long totalExecutionTime, double distributedEfficiency,
                               Map<String, NodeContribution> nodeContributions) {
            this.participatingNodes = participatingNodes;
            this.executedTasks = executedTasks;
            this.networkCommunications = networkCommunications;
            this.totalExecutionTime = totalExecutionTime;
            this.distributedEfficiency = distributedEfficiency;
            this.nodeContributions = nodeContributions;
        }

        public int getParticipatingNodes() { return participatingNodes; }
        public int getExecutedTasks() { return executedTasks; }
        public int getNetworkCommunications() { return networkCommunications; }
        public long getTotalExecutionTime() { return totalExecutionTime; }
        public double getDistributedEfficiency() { return distributedEfficiency; }
        public Map<String, NodeContribution> getNodeContributions() { return nodeContributions; }
    }

    static class NodeResult {
        private final String nodeId;
        private final List<String> results;
        private final int communications;

        public NodeResult(String nodeId, List<String> results, int communications) {
            this.nodeId = nodeId;
            this.results = results;
            this.communications = communications;
        }

        public String getNodeId() { return nodeId; }
        public List<String> getResults() { return results; }
        public int getCommunications() { return communications; }
    }

    static class NodeContribution {
        private final int tasksCompleted;
        private final int communications;
        private final double efficiency;

        public NodeContribution(int tasksCompleted, int communications, double efficiency) {
            this.tasksCompleted = tasksCompleted;
            this.communications = communications;
            this.efficiency = efficiency;
        }

        public int getTasksCompleted() { return tasksCompleted; }
        public int getCommunications() { return communications; }
        public double getEfficiency() { return efficiency; }
    }

    static class SyncResult {
        private final long syncOperations;
        private final double stateConsistency;
        private final long syncLatency;
        private final int conflictResolutions;
        private final List<StateSyncOperation> syncHistory;

        public SyncResult(long syncOperations, double stateConsistency, long syncLatency,
                         int conflictResolutions, List<StateSyncOperation> syncHistory) {
            this.syncOperations = syncOperations;
            this.stateConsistency = stateConsistency;
            this.syncLatency = syncLatency;
            this.conflictResolutions = conflictResolutions;
            this.syncHistory = syncHistory;
        }

        public long getSyncOperations() { return syncOperations; }
        public double getStateConsistency() { return stateConsistency; }
        public long getSyncLatency() { return syncLatency; }
        public int getConflictResolutions() { return conflictResolutions; }
        public List<StateSyncOperation> getSyncHistory() { return syncHistory; }
    }

    static class AgentState {
        private final String agentId;
        private String state;
        private long lastUpdated;

        public AgentState(String agentId, String state) {
            this.agentId = agentId;
            this.state = state;
            this.lastUpdated = System.currentTimeMillis();
        }

        public String getAgentId() { return agentId; }
        public String getState() { return state; }
        public void setState(String state) { this.state = state; }
        public long getLastUpdated() { return lastUpdated; }
        public void setLastUpdated(long lastUpdated) { this.lastUpdated = lastUpdated; }
    }

    static class StateSyncOperation {
        private final String fromAgent;
        private final String toAgent;
        private final String operation;
        private final long timestamp;

        public StateSyncOperation(String fromAgent, String toAgent, String operation, long timestamp) {
            this.fromAgent = fromAgent;
            this.toAgent = toAgent;
            this.operation = operation;
            this.timestamp = timestamp;
        }

        public String getFromAgent() { return fromAgent; }
        public String getToAgent() { return toAgent; }
        public String getOperation() { return operation; }
        public long getTimestamp() { return timestamp; }
    }

    static class ConflictResult {
        private final int detectedConflicts;
        private final int resolvedConflicts;
        private final double resolutionAccuracy;
        private final long averageResolutionTime;
        private final List<ConflictDetail> conflictDetails;

        public ConflictResult(int detectedConflicts, int resolvedConflicts, double resolutionAccuracy,
                            long averageResolutionTime, List<ConflictDetail> conflictDetails) {
            this.detectedConflicts = detectedConflicts;
            this.resolvedConflicts = resolvedConflicts;
            this.resolutionAccuracy = resolutionAccuracy;
            this.averageResolutionTime = averageResolutionTime;
            this.conflictDetails = conflictDetails;
        }

        public int getDetectedConflicts() { return detectedConflicts; }
        public int getResolvedConflicts() { return resolvedConflicts; }
        public double getResolutionAccuracy() { return resolutionAccuracy; }
        public long getAverageResolutionTime() { return averageResolutionTime; }
        public List<ConflictDetail> getConflictDetails() { return conflictDetails; }
    }

    static class Conflict {
        private final String type;
        private final List<String> involvedAgents;
        private final String description;

        public Conflict(String type, List<String> involvedAgents, String description) {
            this.type = type;
            this.involvedAgents = involvedAgents;
            this.description = description;
        }

        public String getType() { return type; }
        public List<String> getInvolvedAgents() { return involvedAgents; }
        public String getDescription() { return description; }
    }

    static class ConflictDetail {
        private final String type;
        private final List<String> involvedAgents;
        private final String resolutionStrategy;
        private final String outcome;

        public ConflictDetail(String type, List<String> involvedAgents,
                            String resolutionStrategy, String outcome) {
            this.type = type;
            this.involvedAgents = involvedAgents;
            this.resolutionStrategy = resolutionStrategy;
            this.outcome = outcome;
        }

        public String getType() { return type; }
        public List<String> getInvolvedAgents() { return involvedAgents; }
        public String getResolutionStrategy() { return resolutionStrategy; }
        public String getOutcome() { return outcome; }
    }
}
