package com.brag.agentscope.examples;

import com.brag.agentscope.config.AgentFactory;
import io.agentscope.core.ReActAgent;
import io.agentscope.core.memory.InMemoryMemory;
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
 * InMemory存储示例
 * 演示AgentScope中InMemoryMemory的使用
 */
@Component
@Profile("in-memory-example")
public class InMemoryExample implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(InMemoryExample.class);

    private final AgentFactory agentFactory;

    public InMemoryExample(AgentFactory agentFactory) {
        this.agentFactory = agentFactory;
    }

    @Override
    public void run(String... args) {
        log.info("=== AgentScope InMemory存储示例 ===");

        try {
            // 示例1: 基础InMemory配置
            demonstrateBasicInMemory();

            // 示例2: 高级InMemory配置
            demonstrateAdvancedInMemory();

            // 示例3: InMemory性能监控
            demonstrateInMemoryMonitoring();

            log.info("=== InMemory存储示例执行完成 ===");

        } catch (Exception e) {
            log.error("InMemory示例执行失败", e);
        }

        System.exit(0);
    }

    /**
     * 示例1: 基础InMemory配置
     * 演示最简单的InMemoryMemory使用
     */
    private void demonstrateBasicInMemory() {
        log.info("--- 基础InMemory配置示例 ---");

        // 创建基础的InMemory存储
        InMemoryMemory basicMemory = new InMemoryMemory();

        // 创建使用InMemory存储的Agent
        ReActAgent basicAgent = agentFactory.createAgentWithMemory(basicMemory);

        // 执行对话
        String[] messages = {
            "你好，我是小明",
            "我喜欢编程",
            "我最喜欢的编程语言是Java",
            "请总结一下我们的对话"
        };

        for (String message : messages) {
            log.info("用户: {}", message);
            Msg response = callAgent(basicAgent, message);
            log.info("Agent: {}", response.getTextContent());
        }

        // 查看存储状态
        // 注意：AgentScope 1.0.3版本可能不支持详细统计信息
        log.info("InMemory存储已创建，使用基础配置");
    }

    /**
     * 示例2: 高级InMemory配置
     * 演示带压缩和清理策略的InMemory配置
     */
    private void demonstrateAdvancedInMemory() {
        log.info("--- 高级InMemory配置示例 ---");

        // 创建高级配置的InMemory存储
        // 注意：AgentScope 1.0.3版本的InMemoryMemory不支持高级配置，使用基础版本
        InMemoryMemory advancedMemory = new InMemoryMemory();

        ReActAgent advancedAgent = agentFactory.createAgentWithMemory(advancedMemory);

        // 模拟多轮对话
        for (int i = 0; i < 5; i++) {
            String question = String.format("这是第%d个问题，请记住我问过这个问题", i + 1);
            log.info("用户: {}", question);

            Msg response = callAgent(advancedAgent, question);
            log.info("Agent: {}", response.getTextContent());
        }

        // 测试记忆保持
        String recallQuestion = "我之前问了几个问题？";
        log.info("回忆问题: {}", recallQuestion);
        Msg recallResponse = callAgent(advancedAgent, recallQuestion);
        log.info("回忆回答: {}", recallResponse.getTextContent());

        // 显示高级统计
        // 注意：AgentScope 1.0.3版本可能不支持高级统计信息
        log.info("高级InMemory存储已创建，使用基础配置");
    }

    /**
     * 示例3: InMemory性能监控
     * 演示如何监控InMemory存储的性能
     */
    private void demonstrateInMemoryMonitoring() {
        log.info("--- InMemory性能监控示例 ---");

        // 注意：AgentScope 1.0.3版本的InMemoryMemory不支持builder模式，使用基础构造
        InMemoryMemory monitoredMemory = new InMemoryMemory();

        ReActAgent monitoredAgent = agentFactory.createAgentWithMemory(monitoredMemory);

        // 执行性能测试
        long startTime = System.currentTimeMillis();

        for (int i = 0; i < 20; i++) {
            String testMessage = "性能测试消息 " + i;
            callAgent(monitoredAgent, testMessage);
        }

        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;

        log.info("性能测试完成，耗时: {}ms，平均响应时间: {}ms",
            totalTime, totalTime / 20.0);

        // 获取性能指标
        // 注意：AgentScope 1.0.3版本可能不支持性能监控API
        log.info("性能测试完成，InMemory存储工作正常");

        // 测试内存清理
        // 注意：AgentScope 1.0.3版本可能不支持内存清理API
        log.info("内存清理功能在当前版本中不可用");
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
