# AI Agent - AgentScope 实现

基于AgentScope框架实现的React Agent项目，提供完整的AI代理开发和工具集成能力。

## 功能特性

- ✅ 基于ReAct推理-行动模式的智能代理
- ✅ 声明式工具定义和动态注册
- ✅ 支持DashScope Qwen等多模型集成
- ✅ 异步工具调用和响应式编程
- ✅ 工具组管理和动态激活
- ✅ 完整的状态管理和持久化
- ✅ 流式响应和实时交互

## 项目结构

```
ai-agent-agentscope/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── brag/
│   │   │           └── agentscope/
│   │   │               ├── agent/          # Agent实现
│   │   │               ├── tool/           # 工具定义
│   │   │               ├── config/         # 配置类
│   │   │               ├── service/        # 业务服务
│   │   │               └── util/           # 工具类
│   │   └── resources/
│   │       ├── application.yml
│   │       └── logback-spring.xml
│   └── test/
│       └── java/
│           └── com/
│               └── brag/
│                   └── agentscope/
├── examples/                               # 示例代码
├── docs/                                   # 项目文档
├── pom.xml                                 # Maven配置
└── README.md
```

## 快速开始

### 环境要求

- JDK 17+
- Maven 3.6+
- AgentScope Java 1.0.7+

### 配置环境变量

```bash
# 设置DashScope API密钥
export DASHSCOPE_API_KEY="your-api-key-here"

# 或者在application.yml中配置
```

### 运行示例

```bash
# 克隆项目
git clone <repository-url>
cd ai-agent-agentscope

# 编译项目
mvn clean compile

# 运行基础示例
mvn exec:java -Dexec.mainClass="com.brag.agentscope.examples.BasicChatExample"

# 运行工具调用示例
mvn exec:java -Dexec.mainClass="com.brag.agentscope.examples.ToolUsageExample"
```

## 核心组件

### ReactAgent

```java
@Autowired
private ReActAgent mathAssistant;

// 使用Agent处理问题
Msg question = Msg.builder()
    .role(MsgRole.USER)
    .textContent("计算 15 + 27 的结果")
    .build();

Msg answer = mathAssistant.call(question).block();
```

### 工具系统

```java
@Service
public class Calculator {
    @Tool(description = "计算两个数的和")
    public int add(@ToolParam(name = "a") int a, @ToolParam(name = "b") int b) {
        return a + b;
    }
}
```

## 配置说明

### application.yml

```yaml
agentscope:
  model:
    provider: dashscope
    api-key: ${DASHSCOPE_API_KEY}
    model-name: qwen-plus
    stream: true
  agent:
    max-iters: 5
    memory-type: in-memory
```

## 示例场景

### 1. 数学计算助手

```java
// 注册数学工具
toolkit.registerTool(new Calculator());
toolkit.registerTool(new MathTools());

// 创建Agent
ReActAgent mathAgent = agentFactory.createMathAssistant(toolkit);

// 使用
Msg result = mathAgent.call(userQuestion).block();
```

### 2. 智能问答系统

```java
// 注册搜索和知识库工具
toolkit.registerTool(new WebSearchTool());
toolkit.registerTool(new KnowledgeBaseTool());

// 创建问答Agent
ReActAgent qaAgent = agentFactory.createQAAssistant(toolkit);
```

## 开发指南

### 添加新工具

1. 创建工具类并使用`@Tool`注解
2. 在配置类中注册工具
3. 更新Agent配置以包含新工具

### 自定义Agent

1. 继承`ReActAgent`或使用Builder模式
2. 配置自定义的系统提示词
3. 设置特定的工具集合和内存策略

## 测试

```bash
# 运行单元测试
mvn test

# 运行集成测试
mvn verify

# 生成测试覆盖率报告
mvn test jacoco:report
```

## 文档

- [AgentScope框架调研报告](../../agentscope/AgentScope框架调研报告.md)
- [AgentScope核心组件使用指南](../../agentscope/AgentScope核心组件使用指南.md)
- [AgentScope工具系统使用指南](../../agentscope/AgentScope工具系统使用指南.md)

## 贡献

欢迎提交Issue和Pull Request来改进项目。

## 许可证

[MIT License](LICENSE)