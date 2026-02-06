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
 * 基础Plan-Execute模式示例
 * 演示AgentScope中基本的计划-执行模式实现
 */
@Component
@Profile("plan-execute-basic-example")
@RequiredArgsConstructor
public class PlanExecuteBasicExample implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(PlanExecuteBasicExample.class);

    private final AgentFactory agentFactory;

    @Override
    public void run(String... args) {
        log.info("=== AgentScope 基础Plan-Execute模式示例 ===");

        try {
            // 示例1: 简单的计划-执行流程
            demonstrateSimplePlanExecute();

            // 示例2: 多步骤任务执行
            demonstrateMultiStepExecution();

            // 示例3: 条件分支执行
            demonstrateConditionalExecution();

            log.info("=== 基础Plan-Execute模式示例执行完成 ===");

        } catch (Exception e) {
            log.error("基础Plan-Execute示例执行失败", e);
        }

        System.exit(0);
    }

    /**
     * 示例1: 简单的计划-执行流程
     * 演示最基本的计划制定和顺序执行
     */
    private void demonstrateSimplePlanExecute() {
        log.info("--- 简单计划-执行流程示例 ---");

        // 创建规划Agent（使用大模型）
        ReActAgent planningAgent = agentFactory.createPlanningAgent();

        // 创建执行Agent（使用标准模型）
        ReActAgent executionAgent = agentFactory.createExecutionAgent();

        // 定义任务
        String task = "帮我规划并执行一个简单的项目：从市场调研到产品发布";

        // 第一步：制定计划
        log.info("任务: {}", task);
        log.info("第一步：制定执行计划");

        Msg planningPrompt = Msg.builder()
            .name("user")
            .role(MsgRole.USER)
            .content(List.of(TextBlock.builder()
                .text("请为以下任务制定详细的执行计划，列出每个步骤的具体内容：\n" + task)
                .build()))
            .build();

        Msg planResult = planningAgent.call(planningPrompt).block();
        String plan = planResult.getTextContent();

        log.info("生成的计划:\n{}", plan);

        // 第二步：解析并执行计划
        log.info("第二步：按计划执行");

        List<String> steps = parsePlanSteps(plan);

        for (int i = 0; i < steps.size(); i++) {
            String step = steps.get(i);
            log.info("执行步骤 {}/{}: {}", i + 1, steps.size(), step);

            // 为每个步骤创建执行提示
            Msg executionPrompt = Msg.builder()
                .name("system")
                .role(MsgRole.SYSTEM)
                .content(List.of(TextBlock.builder()
                    .text(String.format("当前执行计划中的步骤 %d：%s\n请详细执行这个步骤，并提供执行结果。",
                        i + 1, step))
                    .build()))
                .build();

            Msg stepResult = executionAgent.call(executionPrompt).block();
            log.info("步骤执行结果: {}", stepResult.getTextContent());
        }

        log.info("✅ 简单计划-执行流程完成");
    }

    /**
     * 示例2: 多步骤任务执行
     * 演示复杂的多步骤任务分解和执行
     */
    private void demonstrateMultiStepExecution() {
        log.info("--- 多步骤任务执行示例 ---");

        // 创建专门的Agent
        ReActAgent planner = agentFactory.createAdvancedPlanningAgent();
        ReActAgent executor = agentFactory.createAdvancedExecutionAgent();

        // 复杂任务
        String complexTask = "开发一个简单的Web应用：包括用户注册、登录、个人资料管理功能";

        // 生成详细计划
        Msg planRequest = Msg.builder()
            .name("user")
            .role(MsgRole.USER)
            .content(List.of(TextBlock.builder()
                .text("请为这个复杂的软件开发任务制定详细的步骤计划：\n" + complexTask +
                      "\n\n请确保计划包含以下方面：" +
                      "\n- 需求分析" +
                      "\n- 技术选型" +
                      "\n- 数据库设计" +
                      "\n- 后端API开发" +
                      "\n- 前端界面开发" +
                      "\n- 测试和部署")
                .build()))
            .build();

        Msg detailedPlan = planner.call(planRequest).block();
        log.info("详细开发计划:\n{}", detailedPlan.getTextContent());

        // 解析计划步骤
        List<String> developmentSteps = parseDetailedSteps(detailedPlan.getTextContent());

        // 执行每个步骤
        for (int i = 0; i < Math.min(developmentSteps.size(), 6); i++) { // 限制步骤数量
            String step = developmentSteps.get(i);
            log.info("开发步骤 {}/{}: {}", i + 1, developmentSteps.size(), step);

            // 模拟步骤执行
            Msg stepExecution = Msg.builder()
                .name("system")
                .role(MsgRole.SYSTEM)
                .content(List.of(TextBlock.builder()
                    .text(String.format("作为软件开发专家，请详细说明如何执行以下步骤：" +
                        "\n步骤：%s\n\n请提供具体的实施计划和技术细节。", step))
                    .build()))
                .build();

            Msg executionResult = executor.call(stepExecution).block();
            log.info("执行方案: {}", executionResult.getTextContent().substring(0, 200) + "...");

            // 短暂延迟模拟执行时间
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        log.info("✅ 多步骤任务执行完成");
    }

    /**
     * 示例3: 条件分支执行
     * 演示基于条件判断的执行分支
     */
    private void demonstrateConditionalExecution() {
        log.info("--- 条件分支执行示例 ---");

        ReActAgent decisionMaker = agentFactory.createBasicAssistant();
        ReActAgent specialistA = agentFactory.createSpecialistAgent("数据分析");
        ReActAgent specialistB = agentFactory.createSpecialistAgent("内容创作");

        // 定义条件任务
        String conditionalTask = "根据当前市场趋势，决定是开发数据分析工具还是内容创作平台";

        // 决策步骤
        Msg decisionPrompt = Msg.builder()
            .name("user")
            .role(MsgRole.USER)
            .content(List.of(TextBlock.builder()
                .text("请分析当前市场趋势，并决定开发方向：\n" + conditionalTask +
                      "\n\n请明确回答：选择数据分析方向还是内容创作方向？并简要说明理由。")
                .build()))
            .build();

        Msg decision = decisionMaker.call(decisionPrompt).block();
        String decisionText = decision.getTextContent().toLowerCase();

        log.info("决策结果: {}", decision.getTextContent());

        // 基于决策结果选择执行路径
        ReActAgent selectedSpecialist;
        String executionPath;
        String specialistTask;

        if (decisionText.contains("数据分析") || decisionText.contains("data")) {
            selectedSpecialist = specialistA;
            executionPath = "数据分析工具开发";
            specialistTask = "请制定数据分析工具的产品需求文档和开发计划";
        } else {
            selectedSpecialist = specialistB;
            executionPath = "内容创作平台开发";
            specialistTask = "请制定内容创作平台的产品需求文档和开发计划";
        }

        log.info("选择的执行路径: {}", executionPath);

        // 执行选择的路径
        Msg specialistPrompt = Msg.builder()
            .name("system")
            .role(MsgRole.SYSTEM)
            .content(List.of(TextBlock.builder()
                .text("基于市场分析决策：" + decision.getTextContent() +
                      "\n\n你的任务是制定" + executionPath + "的详细计划：\n" + specialistTask)
                .build()))
            .build();

        Msg executionPlan = selectedSpecialist.call(specialistPrompt).block();
        log.info("执行计划:\n{}", executionPlan.getTextContent());

        log.info("✅ 条件分支执行完成");
    }

    /**
     * 解析计划步骤的辅助方法
     */
    private List<String> parsePlanSteps(String planText) {
        // 简单的计划解析逻辑
        List<String> steps = new java.util.ArrayList<>();

        String[] lines = planText.split("\n");
        for (String line : lines) {
            line = line.trim();
            if (line.matches("\\d+\\..*") || line.matches("[-•*].*")) {
                // 移除编号和符号
                String step = line.replaceAll("^\\d+\\.", "")
                                .replaceAll("^[-•*]", "")
                                .trim();
                if (!step.isEmpty()) {
                    steps.add(step);
                }
            }
        }

        // 如果没有找到明确的步骤，创建默认步骤
        if (steps.isEmpty()) {
            steps.add("分析任务需求");
            steps.add("制定实施计划");
            steps.add("执行具体任务");
            steps.add("验证执行结果");
        }

        return steps;
    }

    /**
     * 解析详细步骤的辅助方法
     */
    private List<String> parseDetailedSteps(String planText) {
        List<String> steps = new java.util.ArrayList<>();

        // 更详细的解析逻辑
        String[] sections = planText.split("\n(?=\\d+\\.|[-•*]\\s*[A-Z])");

        for (String section : sections) {
            if (section.trim().length() > 10) { // 过滤太短的内容
                steps.add(section.trim());
            }
        }

        // 如果解析失败，提供默认的开发步骤
        if (steps.size() < 3) {
            steps = List.of(
                "需求分析和功能定义",
                "技术架构设计和技术选型",
                "数据库设计和数据模型",
                "后端API开发",
                "前端界面开发",
                "测试计划制定和执行",
                "部署和发布准备"
            );
        }

        return steps;
    }
}
