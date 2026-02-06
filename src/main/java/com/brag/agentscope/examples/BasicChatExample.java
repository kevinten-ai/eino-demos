package com.brag.agentscope.examples;

import com.brag.agentscope.config.AgentFactory;
import io.agentscope.core.ReActAgent;
import io.agentscope.core.message.Msg;
import io.agentscope.core.message.MsgRole;
import io.agentscope.core.message.TextBlock;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 基础聊天示例
 * 演示如何使用AgentScope创建基本的对话Agent
 */
@Component
@Profile("basic-chat-example")
@RequiredArgsConstructor
public class BasicChatExample implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(BasicChatExample.class);

    private final AgentFactory agentFactory;

    @Override
    public void run(String... args) {
        log.info("=== AgentScope 基础聊天示例 ===");

        try {
            // 创建基础助手Agent
            ReActAgent assistant = agentFactory.createBasicAssistant();

            // 准备用户消息
            Msg userMessage = Msg.builder()
                    .name("user")
                    .role(MsgRole.USER)
                    .content(List.of(TextBlock.builder()
                            .text("你好！请介绍一下你自己。")
                            .build()))
                    .build();

            log.info("发送消息: {}", userMessage.getTextContent());

            // 调用Agent并获取响应
            Msg response = assistant.call(userMessage).block();

            log.info("Agent响应: {}", response.getTextContent());

            // 第二个对话回合
            Msg followUp = Msg.builder()
                    .name("user")
                    .role(MsgRole.USER)
                    .content(List.of(TextBlock.builder()
                            .text("你可以做些什么呢？")
                            .build()))
                    .build();

            log.info("发送消息: {}", followUp.getTextContent());

            Msg response2 = assistant.call(followUp).block();
            log.info("Agent响应: {}", response2.getTextContent());

            log.info("=== 示例执行完成 ===");

        } catch (Exception e) {
            log.error("示例执行失败", e);
        }

        // 退出应用
        System.exit(0);
    }
}


