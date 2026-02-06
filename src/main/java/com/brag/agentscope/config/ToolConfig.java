package com.brag.agentscope.config;

import com.brag.agentscope.tool.Calculator;
import com.brag.agentscope.tool.MathTools;
import com.brag.agentscope.tool.WeatherService;
import io.agentscope.core.tool.Toolkit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * 工具配置类
 */
@Configuration
public class ToolConfig {

    private final Toolkit toolkit;
    private final Calculator calculator;
    private final MathTools mathTools;
    private final WeatherService weatherService;

    @Value("${agentscope.tools.groups:}")
    private List<String> activeGroups;

    public ToolConfig(Toolkit toolkit,
                     Calculator calculator,
                     MathTools mathTools,
                     WeatherService weatherService) {
        this.toolkit = toolkit;
        this.calculator = calculator;
        this.mathTools = mathTools;
        this.weatherService = weatherService;
    }

    /**
     * 初始化工具注册
     */
    @PostConstruct
    public void initializeTools() {
        // 注册所有工具
        toolkit.registerTool(calculator);
        toolkit.registerTool(mathTools);
        toolkit.registerTool(weatherService);
    }
}


