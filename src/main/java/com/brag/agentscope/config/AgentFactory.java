package com.brag.agentscope.config;

import io.agentscope.core.ReActAgent;
import io.agentscope.core.memory.InMemoryMemory;
import io.agentscope.core.memory.Memory;
import io.agentscope.core.model.DashScopeChatModel;
import io.agentscope.core.tool.Toolkit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Agent工厂类
 */
@Component
public class AgentFactory {

    private final DashScopeChatModel model;
    private final Toolkit toolkit;

    @Value("${agentscope.agent.max-iters:5}")
    private int maxIters;

    @Value("${agentscope.agent.memory-type:in-memory}")
    private String memoryType;

    @Value("${agentscope.agent.memory-max-length:100}")
    private int memoryMaxLength;

    public AgentFactory(DashScopeChatModel model, Toolkit toolkit) {
        this.model = model;
        this.toolkit = toolkit;
    }

    /**
     * 创建基础对话Agent
     */
    public ReActAgent createBasicAssistant() {
        return ReActAgent.builder()
                .name("BasicAssistant")
                .sysPrompt("你是一个有帮助的AI助手。请友好、准确地回答用户的问题。")
                .model(model)
                .toolkit(new Toolkit()) // 不包含工具的基础版本
                .memory(createMemory())
                // .maxIterations() 方法在AgentScope 1.0.3中不存在，使用默认值
                .build();
    }

    /**
     * 创建数学助手Agent
     */
    public ReActAgent createMathAssistant() {
        return ReActAgent.builder()
                .name("MathAssistant")
                .sysPrompt("你是一个专业的数学助手，可以使用提供的数学工具来解决计算问题。请准确地执行计算并解释结果。")
                .model(model)
                .toolkit(toolkit)
                .memory(createMemory())
                // .maxIterations() 方法在AgentScope 1.0.3中不存在，使用默认值
                .build();
    }

    /**
     * 创建通用助手Agent（包含所有工具）
     */
    public ReActAgent createGeneralAssistant() {
        return ReActAgent.builder()
                .name("GeneralAssistant")
                .sysPrompt("你是一个智能助手，可以使用各种工具来帮助用户解决问题。请根据用户的需求选择合适的工具。")
                .model(model)
                .toolkit(toolkit)
                .memory(createMemory())
                // .maxIterations() 方法在AgentScope 1.0.3中不存在，使用默认值
                .build();
    }

    /**
     * 创建自定义Agent
     */
    public ReActAgent createCustomAgent(String name, String sysPrompt, Toolkit customToolkit) {
        return ReActAgent.builder()
                .name(name)
                .sysPrompt(sysPrompt)
                .model(model)
                .toolkit(customToolkit != null ? customToolkit : toolkit)
                .memory(createMemory())
                // .maxIterations() 方法在AgentScope 1.0.3中不存在，使用默认值
                .build();
    }

    // 新增的Agent工厂方法，用于支持各种示例

    /**
     * 创建使用指定内存的Agent
     */
    public ReActAgent createAgentWithMemory(Memory memory) {
        return ReActAgent.builder()
                .name("MemoryAgent")
                .sysPrompt("你是一个智能助手，可以管理对话记忆。")
                .model(model)
                .toolkit(toolkit)
                .memory(memory)
                // .maxIterations() 方法在AgentScope 1.0.3中不存在，使用默认值
                .build();
    }

    /**
     * 创建MCP支持的Agent
     */
    public ReActAgent createMcpEnabledAgent() {
        return ReActAgent.builder()
                .name("McpAgent")
                .sysPrompt("你是一个支持MCP工具的智能助手，可以使用外部工具来解决问题。")
                .model(model)
                .toolkit(toolkit) // 假设toolkit已配置MCP客户端
                .memory(createMemory())
                // .maxIterations() 方法在AgentScope 1.0.3中不存在，使用默认值
                .build();
    }

    /**
     * 创建多服务器MCP Agent
     */
    public ReActAgent createMultiServerMcpAgent() {
        return ReActAgent.builder()
                .name("MultiServerMcpAgent")
                .sysPrompt("你是一个支持多服务器MCP工具的智能助手，可以跨多个服务器调用工具。")
                .model(model)
                .toolkit(toolkit)
                .memory(createMemory())
                // .maxIterations() 方法在AgentScope 1.0.3中不存在，使用默认值
                .build();
    }

    /**
     * 创建安全MCP Agent
     */
    public ReActAgent createSecureMcpAgent(java.util.List<String> clientIds) {
        return ReActAgent.builder()
                .name("SecureMcpAgent")
                .sysPrompt("你是一个安全的MCP工具助手，注重数据安全和访问控制。")
                .model(model)
                .toolkit(toolkit)
                .memory(createMemory())
                // .maxIterations() 方法在AgentScope 1.0.3中不存在，使用默认值
                .build();
    }

    /**
     * 创建动态MCP Agent
     */
    public ReActAgent createDynamicMcpAgent() {
        return ReActAgent.builder()
                .name("DynamicMcpAgent")
                .sysPrompt("你是一个动态的MCP工具助手，可以根据任务需求切换不同的工具组。")
                .model(model)
                .toolkit(toolkit)
                .memory(createMemory())
                // .maxIterations() 方法在AgentScope 1.0.3中不存在，使用默认值
                .build();
    }

    /**
     * 创建MCP文件Agent
     */
    public ReActAgent createMcpFileAgent(String clientId) {
        return ReActAgent.builder()
                .name("FileAgent-" + clientId)
                .sysPrompt("你是一个文件处理助手，可以使用MCP文件工具管理系统文件。")
                .model(model)
                .toolkit(toolkit)
                .memory(createMemory())
                // .maxIterations() 方法在AgentScope 1.0.3中不存在，使用默认值
                .build();
    }

    /**
     * 创建规划Agent
     */
    public ReActAgent createPlanningAgent() {
        return ReActAgent.builder()
                .name("PlanningAgent")
                .sysPrompt("你是一个专业的任务规划师，擅长制定详细的执行计划。")
                .model(model)
                .toolkit(new Toolkit()) // 规划阶段通常不需要复杂工具
                .memory(createMemory())
                // .maxIterations() 方法在AgentScope 1.0.3中不存在，使用默认值
                .build();
    }

    /**
     * 创建高级规划Agent
     */
    public ReActAgent createAdvancedPlanningAgent() {
        return ReActAgent.builder()
                .name("AdvancedPlanningAgent")
                .sysPrompt("你是一个高级规划师，可以制定复杂项目的详细执行计划。")
                .model(model)
                .toolkit(toolkit)
                .memory(createMemory())
                .maxIters(maxIters + 2) // 更复杂的规划需要更多迭代
                .build();
    }

    /**
     * 创建执行Agent
     */
    public ReActAgent createExecutionAgent() {
        return ReActAgent.builder()
                .name("ExecutionAgent")
                .sysPrompt("你是一个执行专家，擅长按照计划执行具体任务。")
                .model(model)
                .toolkit(toolkit)
                .memory(createMemory())
                // .maxIterations() 方法在AgentScope 1.0.3中不存在，使用默认值
                .build();
    }

    /**
     * 创建高级执行Agent
     */
    public ReActAgent createAdvancedExecutionAgent() {
        return ReActAgent.builder()
                .name("AdvancedExecutionAgent")
                .sysPrompt("你是一个高级执行专家，可以处理复杂的任务执行和问题解决。")
                .model(model)
                .toolkit(toolkit)
                .memory(createMemory())
                .maxIters(maxIters + 3) // 复杂执行需要更多迭代
                .build();
    }

    /**
     * 创建对话Agent
     */
    public ReActAgent createConversationalAgent() {
        return ReActAgent.builder()
                .name("ConversationalAgent")
                .sysPrompt("你是一个友好的对话助手，可以进行自然流畅的对话。")
                .model(model)
                .toolkit(toolkit)
                .memory(createMemory())
                // .maxIterations() 方法在AgentScope 1.0.3中不存在，使用默认值
                .build();
    }

    /**
     * 创建特殊领域Agent
     */
    public ReActAgent createSpecialistAgent(String domain) {
        String sysPrompt = String.format("你是一个%s领域的专家，擅长解决相关问题。", domain);
        return ReActAgent.builder()
                .name(domain + "Specialist")
                .sysPrompt(sysPrompt)
                .model(model)
                .toolkit(toolkit)
                .memory(createMemory())
                // .maxIterations() 方法在AgentScope 1.0.3中不存在，使用默认值
                .build();
    }

    /**
     * 创建任务分配器Agent
     */
    public ReActAgent createTaskAllocatorAgent() {
        return ReActAgent.builder()
                .name("TaskAllocator")
                .sysPrompt("你是一个任务分配专家，可以根据Agent能力和当前负载智能分配任务。")
                .model(model)
                .toolkit(new Toolkit())
                .memory(createMemory())
                // .maxIterations() 方法在AgentScope 1.0.3中不存在，使用默认值
                .build();
    }

    /**
     * 创建决策Agent
     */
    public ReActAgent createDecisionMakerAgent() {
        return ReActAgent.builder()
                .name("DecisionMaker")
                .sysPrompt("你是一个决策专家，可以分析各种选择并做出最佳决策。")
                .model(model)
                .toolkit(new Toolkit())
                .memory(createMemory())
                // .maxIterations() 方法在AgentScope 1.0.3中不存在，使用默认值
                .build();
    }

    /**
     * 创建冲突解决Agent
     */
    public ReActAgent createConflictResolverAgent() {
        return ReActAgent.builder()
                .name("ConflictResolver")
                .sysPrompt("你是一个冲突解决专家，可以分析问题并提出解决方案。")
                .model(model)
                .toolkit(toolkit)
                .memory(createMemory())
                // .maxIterations() 方法在AgentScope 1.0.3中不存在，使用默认值
                .build();
    }

    /**
     * 创建结果整合Agent
     */
    public ReActAgent createResultIntegratorAgent() {
        return ReActAgent.builder()
                .name("ResultIntegrator")
                .sysPrompt("你是一个结果整合专家，可以综合多个来源的信息形成完整答案。")
                .model(model)
                .toolkit(new Toolkit())
                .memory(createMemory())
                // .maxIterations() 方法在AgentScope 1.0.3中不存在，使用默认值
                .build();
    }

    /**
     * 创建工作流规划Agent
     */
    public ReActAgent createWorkflowPlannerAgent() {
        return ReActAgent.builder()
                .name("WorkflowPlanner")
                .sysPrompt("你是一个工作流规划专家，可以设计复杂的工作流程。")
                .model(model)
                .toolkit(new Toolkit())
                .memory(createMemory())
                // .maxIterations() 方法在AgentScope 1.0.3中不存在，使用默认值
                .build();
    }

    /**
     * 创建推荐Agent
     */
    public ReActAgent createRecommendationAgent() {
        return ReActAgent.builder()
                .name("RecommendationAgent")
                .sysPrompt("你是一个推荐专家，可以基于用户偏好提供个性化推荐。")
                .model(model)
                .toolkit(new Toolkit())
                .memory(createMemory())
                // .maxIterations() 方法在AgentScope 1.0.3中不存在，使用默认值
                .build();
    }

    /**
     * 创建优化Agent
     */
    public ReActAgent createOptimizationAgent() {
        return ReActAgent.builder()
                .name("OptimizationAgent")
                .sysPrompt("你是一个优化专家，可以分析和改进各种方案。")
                .model(model)
                .toolkit(toolkit)
                .memory(createMemory())
                // .maxIterations() 方法在AgentScope 1.0.3中不存在，使用默认值
                .build();
    }

    /**
     * 创建自适应Agent
     */
    public ReActAgent createAdaptiveAgent() {
        return ReActAgent.builder()
                .name("AdaptiveAgent")
                .sysPrompt("你是一个自适应助手，可以根据用户偏好调整自己的行为。")
                .model(model)
                .toolkit(toolkit)
                .memory(createMemory())
                // .maxIterations() 方法在AgentScope 1.0.3中不存在，使用默认值
                .build();
    }

    /**
     * 创建协调器Agent
     */
    public ReActAgent createCoordinatorAgent() {
        return ReActAgent.builder()
                .name("Coordinator")
                .sysPrompt("你是一个协调专家，可以管理多Agent协作。")
                .model(model)
                .toolkit(toolkit)
                .memory(createMemory())
                // .maxIterations() 方法在AgentScope 1.0.3中不存在，使用默认值
                .build();
    }

    /**
     * 根据配置创建内存组件
     * 注意：AgentScope 1.0.3版本中没有LimitedMemory，使用InMemoryMemory作为默认
     */
    private Memory createMemory() {
        // AgentScope 1.0.3仅支持InMemoryMemory
        return new InMemoryMemory();
    }
}


