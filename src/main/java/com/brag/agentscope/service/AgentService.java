package com.brag.agentscope.service;

import com.brag.agentscope.config.AgentFactory;
import io.agentscope.core.ReActAgent;
import io.agentscope.core.message.Msg;
import io.agentscope.core.message.MsgRole;
import io.agentscope.core.message.TextBlock;
import io.agentscope.core.tool.Toolkit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;

/**
 * Agent服务类
 * 提供Agent调用和管理功能
 */
@Service
@RequiredArgsConstructor
public class AgentService {

    private static final Logger log = LoggerFactory.getLogger(AgentService.class);

    private final AgentFactory agentFactory;
    private final Toolkit toolkit;

    /**
     * 处理基础对话请求
     */
    public Mono<Msg> chat(String userMessage) {
        return chat(userMessage, AgentType.BASIC);
    }

    /**
     * 处理指定类型的对话请求
     */
    public Mono<Msg> chat(String userMessage, AgentType agentType) {
        ReActAgent agent = getAgentByType(agentType);

        Msg userMsg = Msg.builder()
                .name("user")
                .role(MsgRole.USER)
                .content(List.of(TextBlock.builder().text(userMessage).build()))
                .build();

        log.info("Processing chat request with agent type: {}, message: {}", agentType, userMessage);

        return agent.call(userMsg)
                .timeout(Duration.ofSeconds(30))
                .doOnNext(response -> log.info("Agent response: {}", response.getTextContent()))
                .doOnError(error -> log.error("Agent processing error", error));
    }

    /**
     * 处理数学相关问题
     */
    public Mono<Msg> solveMath(String mathProblem) {
        return chat(mathProblem, AgentType.MATH);
    }

    /**
     * 处理通用助手请求（包含所有工具）
     */
    public Mono<Msg> generalAssistant(String request) {
        return chat(request, AgentType.GENERAL);
    }

    /**
     * 获取工具列表信息
     */
    public String getAvailableTools() {
        StringBuilder info = new StringBuilder();
        info.append("可用的工具：\n");

        toolkit.getToolSchemas().forEach(schema -> {
            info.append(String.format("- %s: %s\n",
                    schema.getName(),
                    schema.getDescription()));
        });

        return info.toString();
    }

    /**
     * 获取激活的工具组信息
     */
    public String getActivatedToolGroups() {
        // AgentScope 1.0.3中没有工具组激活的概念，返回基本信息
        return "工具组管理功能在当前版本中不可用，所有注册的工具都处于激活状态。";
    }

    /**
     * 根据类型获取Agent实例
     */
    private ReActAgent getAgentByType(AgentType agentType) {
        switch (agentType) {
            case BASIC:
                return agentFactory.createBasicAssistant();
            case MATH:
                return agentFactory.createMathAssistant();
            case GENERAL:
            default:
                return agentFactory.createGeneralAssistant();
        }
    }

    /**
     * Agent类型枚举
     */
    public enum AgentType {
        BASIC("基础对话助手"),
        MATH("数学计算助手"),
        GENERAL("通用智能助手");

        private final String description;

        AgentType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }
}


