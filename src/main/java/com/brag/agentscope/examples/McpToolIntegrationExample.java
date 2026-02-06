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

/**
 * MCP Tool集成示例
 * 演示如何在AgentScope中集成和使用MCP工具
 */
@Component
@Profile("mcp-tool-integration-example")
@RequiredArgsConstructor
public class McpToolIntegrationExample implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(McpToolIntegrationExample.class);

    private final AgentFactory agentFactory;

    @Override
    public void run(String... args) {
        log.info("=== AgentScope MCP Tool集成示例 ===");

        try {
            // 示例1: 基础MCP工具集成
            demonstrateBasicMcpIntegration();

            // 示例2: 多MCP服务器集成
            demonstrateMultipleMcpServers();

            // 示例3: MCP工具动态切换
            demonstrateDynamicToolSwitching();

            log.info("=== MCP Tool集成示例执行完成 ===");

        } catch (Exception e) {
            log.error("MCP集成示例执行失败", e);
        }

        System.exit(0);
    }

    /**
     * 示例1: 基础MCP工具集成
     * 演示如何集成单个MCP服务器的工具
     */
    private void demonstrateBasicMcpIntegration() {
        log.info("--- 基础MCP工具集成示例 ---");

        try {
            // 注意：这里假设已经配置了MCP客户端
            // 在实际使用中，需要先配置MCP服务器连接

            // 创建支持MCP工具的Agent
            ReActAgent mcpEnabledAgent = agentFactory.createMcpEnabledAgent();

            // 示例任务：使用文件系统工具
            String fileTask = "请帮我查看当前目录下的文件列表";
            log.info("任务: {}", fileTask);

            Msg response = callAgent(mcpEnabledAgent, fileTask);
            log.info("Agent回答: {}", response.getTextContent());

        } catch (Exception e) {
            log.warn("基础MCP集成示例跳过（需要MCP服务器配置）: {}", e.getMessage());
        }
    }

    /**
     * 示例2: 多MCP服务器集成
     * 演示如何同时集成多个MCP服务器
     */
    private void demonstrateMultipleMcpServers() {
        log.info("--- 多MCP服务器集成示例 ---");

        try {
            // 创建支持多服务器MCP工具的Agent
            ReActAgent multiServerAgent = agentFactory.createMultiServerMcpAgent();

            // 示例任务：跨服务器调用
            String multiServerTask = "请先查询数据库中的用户数量，然后检查日志文件中是否有错误信息";
            log.info("任务: {}", multiServerTask);

            Msg response = callAgent(multiServerAgent, multiServerTask);
            log.info("Agent回答: {}", response.getTextContent());

        } catch (Exception e) {
            log.warn("多服务器MCP集成示例跳过（需要多服务器配置）: {}", e.getMessage());
        }
    }

    /**
     * 示例3: MCP工具动态切换
     * 演示如何根据任务类型动态激活不同的工具组
     */
    private void demonstrateDynamicToolSwitching() {
        log.info("--- MCP工具动态切换示例 ---");

        try {
            // 创建支持动态工具切换的Agent
            ReActAgent dynamicAgent = agentFactory.createDynamicMcpAgent();

            // 任务1：数据处理任务（激活数据库工具）
            String dataTask = "请查询数据库中最近7天的用户注册数据";
            log.info("数据任务: {}", dataTask);

            Msg dataResponse = callAgent(dynamicAgent, dataTask);
            log.info("数据任务回答: {}", dataResponse.getTextContent());

            // 任务2：文件处理任务（激活文件系统工具）
            String fileTask = "请分析日志文件中的错误模式";
            log.info("文件任务: {}", fileTask);

            Msg fileResponse = callAgent(dynamicAgent, fileTask);
            log.info("文件任务回答: {}", fileResponse.getTextContent());

        } catch (Exception e) {
            log.warn("动态工具切换示例跳过（需要完整MCP配置）: {}", e.getMessage());
        }
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
