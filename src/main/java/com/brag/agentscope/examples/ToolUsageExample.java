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

/**
 * 工具使用示例
 * 演示如何使用AgentScope的工具调用功能
 */
@Component
@Profile("tool-usage-example")
@RequiredArgsConstructor
public class ToolUsageExample implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(ToolUsageExample.class);

    private final AgentFactory agentFactory;

    @Override
    public void run(String... args) {
        log.info("=== AgentScope 工具使用示例 ===");

        try {
            // 创建数学助手Agent（包含计算工具）
            ReActAgent mathAssistant = agentFactory.createMathAssistant();

            // 示例1: 基础数学计算
            String mathProblem1 = "计算 15 + 27 的结果";
            log.info("数学问题1: {}", mathProblem1);

            Msg response1 = callAgent(mathAssistant, mathProblem1);
            log.info("Agent回答1: {}", response1.getTextContent());

            // 示例2: 复杂数学计算
            String mathProblem2 = "先计算5的阶乘，然后告诉我这个结果是否为质数";
            log.info("数学问题2: {}", mathProblem2);

            Msg response2 = callAgent(mathAssistant, mathProblem2);
            log.info("Agent回答2: {}", response2.getTextContent());

            // 示例3: 多步骤计算
            String mathProblem3 = "计算 (10 + 5) * 3 - 8 的结果";
            log.info("数学问题3: {}", mathProblem3);

            Msg response3 = callAgent(mathAssistant, mathProblem3);
            log.info("Agent回答3: {}", response3.getTextContent());

            // 创建通用助手Agent（包含所有工具）
            ReActAgent generalAssistant = agentFactory.createGeneralAssistant();

            // 示例4: 综合问题解决
            String complexProblem = "今天北京的天气怎么样？另外，请帮我计算一下 25 的平方根。";
            log.info("综合问题: {}", complexProblem);

            Msg response4 = callAgent(generalAssistant, complexProblem);
            log.info("Agent回答4: {}", response4.getTextContent());

            log.info("=== 工具使用示例执行完成 ===");

        } catch (Exception e) {
            log.error("示例执行失败", e);
        }

        // 退出应用
        System.exit(0);
    }

    /**
     * 调用Agent的辅助方法
     */
    private Msg callAgent(ReActAgent agent, String message) {
        Msg userMsg = Msg.builder()
                .name("user")
                .role(MsgRole.USER)
                .content(List.of(TextBlock.builder().text(message).build()))
                .build();

        return agent.call(userMsg).block();
    }
}


