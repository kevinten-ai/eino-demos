package com.brag.agentscope.examples;

import com.brag.agentscope.service.AgentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Scanner;

/**
 * 交互式示例
 * 提供命令行交互界面来测试Agent功能
 */
@Component
@Profile("interactive-example")
@RequiredArgsConstructor
public class InteractiveExample implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(InteractiveExample.class);

    private final AgentService agentService;

    @Override
    public void run(String... args) {
        log.info("=== AgentScope 交互式示例 ===");
        log.info("输入 'quit' 退出，输入 'help' 查看帮助");

        Scanner scanner = new Scanner(System.in);

        try {
            while (true) {
                System.out.print("\n请选择Agent类型 (basic/math/general): ");
                String agentType = scanner.nextLine().trim().toLowerCase();

                if ("quit".equals(agentType) || "exit".equals(agentType)) {
                    break;
                }

                if ("help".equals(agentType)) {
                    showHelp();
                    continue;
                }

                AgentService.AgentType type;
                switch (agentType) {
                    case "basic":
                        type = AgentService.AgentType.BASIC;
                        break;
                    case "math":
                        type = AgentService.AgentType.MATH;
                        break;
                    case "general":
                        type = AgentService.AgentType.GENERAL;
                        break;
                    default:
                        System.out.println("无效的Agent类型，请重新输入");
                        continue;
                }

                System.out.print("请输入您的问题: ");
                String question = scanner.nextLine().trim();

                if (question.isEmpty()) {
                    continue;
                }

                System.out.println("正在思考中...");
                long startTime = System.currentTimeMillis();

                try {
                    var response = agentService.chat(question, type).block();
                    long endTime = System.currentTimeMillis();

                    System.out.println("\n=== Agent回答 ===");
                    System.out.println(response.getTextContent());
                    System.out.println("耗时: " + (endTime - startTime) + "ms");

                } catch (Exception e) {
                    System.out.println("处理请求时出错: " + e.getMessage());
                    log.error("Agent处理错误", e);
                }
            }

        } finally {
            scanner.close();
        }

        log.info("=== 交互式示例结束 ===");
        System.exit(0);
    }

    private void showHelp() {
        System.out.println("\n=== 帮助信息 ===");
        System.out.println("Agent类型:");
        System.out.println("  basic   - 基础对话助手（无工具）");
        System.out.println("  math    - 数学计算助手（包含计算工具）");
        System.out.println("  general - 通用智能助手（包含所有工具）");
        System.out.println("\n命令:");
        System.out.println("  help - 显示帮助信息");
        System.out.println("  quit/exit - 退出程序");
        System.out.println("\n示例问题:");
        System.out.println("  基础对话: 你好，请介绍一下你自己");
        System.out.println("  数学计算: 计算 15 + 27 的结果");
        System.out.println("  天气查询: 北京现在的天气怎么样？");
    }
}

