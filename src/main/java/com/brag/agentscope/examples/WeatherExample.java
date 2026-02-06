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
 * 天气查询示例
 * 演示如何使用AgentScope的天气工具
 */
@Component
@Profile("weather-example")
public class WeatherExample implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(WeatherExample.class);

    private final AgentFactory agentFactory;

    public WeatherExample(AgentFactory agentFactory) {
        this.agentFactory = agentFactory;
    }

    @Override
    public void run(String... args) {
        log.info("=== AgentScope 天气查询示例 ===");

        try {
            // 创建通用助手Agent（包含天气工具）
            ReActAgent assistant = agentFactory.createGeneralAssistant();

            // 示例1: 查询当前天气
            String weatherQuery1 = "北京现在的天气怎么样？";
            log.info("天气查询1: {}", weatherQuery1);

            Msg response1 = callAgent(assistant, weatherQuery1);
            log.info("Agent回答1: {}", response1.getTextContent());

            // 示例2: 查询天气预报
            String weatherQuery2 = "帮我查看一下上海未来3天的天气预报";
            log.info("天气查询2: {}", weatherQuery2);

            Msg response2 = callAgent(assistant, weatherQuery2);
            log.info("Agent回答2: {}", response2.getTextContent());

            // 示例3: 比较城市天气
            String weatherQuery3 = "比较一下广州和深圳今天的天气有什么不同";
            log.info("天气查询3: {}", weatherQuery3);

            Msg response3 = callAgent(assistant, weatherQuery3);
            log.info("Agent回答3: {}", response3.getTextContent());

            // 示例4: 空气质量查询
            String weatherQuery4 = "杭州的空气质量怎么样？";
            log.info("天气查询4: {}", weatherQuery4);

            Msg response4 = callAgent(assistant, weatherQuery4);
            log.info("Agent回答4: {}", response4.getTextContent());

            log.info("=== 天气查询示例执行完成 ===");

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


