# AgentScope 示例指南

本文档介绍如何运行和使用ai-agent-agentscope项目的各种示例。

## 示例概述

项目提供了多种类型的示例，展示AgentScope框架的不同功能：

1. **BasicChatExample** - 基础对话示例
2. **ToolUsageExample** - 工具使用示例
3. **WeatherExample** - 天气查询示例
4. **InteractiveExample** - 交互式示例
5. **AdvancedExample** - 高级功能示例

## 环境准备

### 1. 设置API密钥

在运行示例前，请确保设置了DashScope API密钥：

```bash
# Linux/macOS
export DASHSCOPE_API_KEY="your-api-key-here"

# Windows PowerShell
$env:DASHSCOPE_API_KEY="your-api-key-here"

# Windows CMD
set DASHSCOPE_API_KEY=your-api-key-here
```

或者在 `src/main/resources/application.yml` 中配置：

```yaml
agentscope:
  model:
    api-key: your-api-key-here
```

### 2. 编译项目

```bash
mvn clean compile
```

## 运行示例

### 基础对话示例

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=basic-chat-example
```

这个示例演示了：
- 创建基本的ReActAgent
- 进行简单的问答对话
- 展示Agent的推理-行动循环

### 工具使用示例

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=tool-usage-example
```

这个示例演示了：
- 数学计算工具的使用
- 工具调用和结果处理
- 多步骤问题解决

### 天气查询示例

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=weather-example
```

这个示例演示了：
- 天气服务的各种功能
- 城市天气查询
- 天气预报功能
- 空气质量查询

### 交互式示例

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=interactive-example
```

这个示例提供命令行交互界面：
- 选择不同的Agent类型
- 实时对话
- 性能计时

**使用方法：**
```
请选择Agent类型 (basic/math/general): math
请输入您的问题: 计算 15 + 27 的结果
```

### 高级功能示例

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=advanced-example
```

这个示例演示了：
- 多轮对话
- 工具组动态切换
- 复杂问题解决
- 错误处理和边界情况

## 示例代码结构

```
src/main/java/com/brag/agentscope/examples/
├── BasicChatExample.java          # 基础对话
├── ToolUsageExample.java          # 工具使用
├── WeatherExample.java            # 天气查询
├── InteractiveExample.java        # 交互式界面
└── AdvancedExample.java           # 高级功能
```

## REST API 示例

除了命令行示例，还可以通过REST API测试功能：

### 启动Web服务

```bash
mvn spring-boot:run
```

### 测试API

```bash
# 基础对话
curl -X POST http://localhost:8080/api/agent/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "你好，请介绍一下你自己"}'

# 数学助手
curl -X POST http://localhost:8080/api/agent/math \
  -H "Content-Type: application/json" \
  -d '{"message": "计算 15 + 27 的结果"}'

# 通用助手
curl -X POST http://localhost:8080/api/agent/general \
  -H "Content-Type: application/json" \
  -d '{"message": "北京的天气怎么样？"}'

# 查看可用工具
curl http://localhost:8080/api/agent/tools

# 健康检查
curl http://localhost:8080/api/agent/health
```

## 自定义示例

### 创建新的示例

1. 在 `examples` 包中创建新的类
2. 实现 `CommandLineRunner` 接口
3. 使用 `@Profile` 注解指定profile名称
4. 在 `AgentFactory` 中创建合适的Agent实例

```java
@Component
@Profile("my-custom-example")
public class MyCustomExample implements CommandLineRunner {
    // 实现示例逻辑
}
```

### 运行自定义示例

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=my-custom-example
```

## 测试示例

### 运行单元测试

```bash
mvn test
```

### 运行特定测试类

```bash
mvn test -Dtest=CalculatorTest
```

### 生成测试覆盖率报告

```bash
mvn test jacoco:report
# 查看报告：target/site/jacoco/index.html
```

## 故障排除

### 常见问题

1. **API密钥未设置**
   ```
   IllegalArgumentException: DASHSCOPE_API_KEY is required
   ```
   解决方案：设置环境变量或在配置文件中配置API密钥

2. **网络连接问题**
   ```
   Connection timeout
   ```
   解决方案：检查网络连接和DashScope服务状态

3. **内存不足**
   ```
   OutOfMemoryError
   ```
   解决方案：增加JVM内存 `-Xmx2g`

4. **依赖问题**
   ```
   ClassNotFoundException
   ```
   解决方案：重新编译项目 `mvn clean compile`

### 日志调试

启用DEBUG日志查看详细信息：

```yaml
logging:
  level:
    com.brag.agentscope: DEBUG
    io.agentscope: DEBUG
```

## 性能优化建议

1. **连接池配置**：合理配置HTTP连接池避免频繁创建连接
2. **缓存策略**：对稳定数据实现缓存减少API调用
3. **异步处理**：使用异步工具提高并发性能
4. **内存管理**：监控内存使用，及时清理不需要的数据

## 扩展示例

项目提供了良好的扩展性，可以基于现有框架：

- 添加新的工具类
- 实现自定义的Agent类型
- 集成其他LLM模型
- 添加新的业务场景

参考 `config` 和 `tool` 包的实现来扩展功能。


