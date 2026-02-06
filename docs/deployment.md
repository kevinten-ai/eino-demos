# 部署指南

本文档介绍如何将ai-agent-agentscope项目部署到不同环境中。

## 系统要求

### 最低系统要求
- **操作系统**: Linux, macOS, Windows
- **JDK**: 17+
- **内存**: 2GB RAM
- **磁盘**: 500MB 可用空间
- **网络**: 稳定的互联网连接

### 推荐配置
- **CPU**: 2核心+
- **内存**: 4GB RAM
- **磁盘**: 1GB 可用空间
- **网络**: 10Mbps+ 带宽

## 快速开始

### 使用Maven运行

1. **克隆项目**
```bash
git clone <repository-url>
cd ai-agent-agentscope
```

2. **配置环境**
```bash
# 设置API密钥
export DASHSCOPE_API_KEY="your-api-key-here"
```

3. **运行应用**
```bash
mvn spring-boot:run
```

4. **验证部署**
```bash
curl http://localhost:8080/api/agent/health
```

## 生产部署

### JAR包构建

1. **构建可执行JAR**
```bash
mvn clean package -DskipTests
```

2. **运行JAR包**
```bash
java -jar target/ai-agent-agentscope-1.0.0-SNAPSHOT.jar
```

### Docker部署

#### Dockerfile

```dockerfile
FROM openjdk:17-jdk-slim

WORKDIR /app

# 复制JAR文件
COPY target/ai-agent-agentscope-1.0.0-SNAPSHOT.jar app.jar

# 创建非root用户
RUN groupadd -r agentscope && useradd -r -g agentscope agentscope
USER agentscope

# 暴露端口
EXPOSE 8080

# 健康检查
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD curl -f http://localhost:8080/api/agent/health || exit 1

# 启动应用
ENTRYPOINT ["java", "-jar", "app.jar"]
```

#### 构建和运行

```bash
# 构建镜像
docker build -t ai-agent-agentscope:latest .

# 运行容器
docker run -d \
  --name agentscope-app \
  -p 8080:8080 \
  -e DASHSCOPE_API_KEY=your-api-key-here \
  ai-agent-agentscope:latest
```

### Docker Compose部署

#### docker-compose.yml

```yaml
version: '3.8'

services:
  agentscope-app:
    image: ai-agent-agentscope:latest
    ports:
      - "8080:8080"
    environment:
      - DASHSCOPE_API_KEY=${DASHSCOPE_API_KEY}
      - SPRING_PROFILES_ACTIVE=production
    restart: unless-stopped
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8080/api/agent/health"]
      interval: 30s
      timeout: 10s
      retries: 3
      start_period: 40s
    networks:
      - agentscope-network

networks:
  agentscope-network:
    driver: bridge
```

#### 环境变量文件

创建 `.env` 文件：

```bash
DASHSCOPE_API_KEY=your-actual-api-key-here
```

#### 运行服务

```bash
# 启动服务
docker-compose up -d

# 查看日志
docker-compose logs -f agentscope-app

# 停止服务
docker-compose down
```

## 配置管理

### 环境变量

| 变量名 | 描述 | 默认值 | 必需 |
|--------|------|--------|------|
| `DASHSCOPE_API_KEY` | DashScope API密钥 | - | 是 |
| `SPRING_PROFILES_ACTIVE` | 激活的Spring配置 | development | 否 |
| `SERVER_PORT` | 服务端口 | 8080 | 否 |
| `JAVA_OPTS` | JVM参数 | - | 否 |

### 配置文件

#### application.yml (基础配置)

```yaml
spring:
  application:
    name: ai-agent-agentscope
  profiles:
    active: production

agentscope:
  model:
    provider: dashscope
    api-key: ${DASHSCOPE_API_KEY}
    model-name: qwen-plus
    stream: true
    enable-thinking: true
  agent:
    max-iters: 5

server:
  port: 8080

logging:
  level:
    com.brag.agentscope: INFO
    io.agentscope: INFO
```

#### application-production.yml (生产配置)

```yaml
spring:
  profiles: production

server:
  port: 8080

logging:
  level:
    com.brag.agentscope: WARN
    io.agentscope: WARN
  file:
    name: /app/logs/agentscope.log

management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
  endpoint:
    health:
      show-details: when-authorized

agentscope:
  agent:
    max-iters: 10  # 生产环境允许更多推理步骤
```

## 监控和日志

### 健康检查

```bash
# HTTP健康检查
curl http://localhost:8080/api/agent/health

# 详细健康信息
curl http://localhost:8080/actuator/health
```

### 应用指标

```bash
# 查看所有指标
curl http://localhost:8080/actuator/metrics

# 查看HTTP请求指标
curl http://localhost:8080/actuator/metrics/http.server.requests

# 查看JVM内存使用
curl http://localhost:8080/actuator/metrics/jvm.memory.used
```

### 日志配置

#### logback-spring.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <include resource="org/springframework/boot/logging/logback/defaults.xml"/>

    <!-- 控制台输出 -->
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>

    <!-- 文件输出 -->
    <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>logs/agentscope.log</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>logs/agentscope.%d{yyyy-MM-dd}.%i.log</fileNamePattern>
            <maxFileSize>100MB</maxFileSize>
            <maxHistory>30</maxHistory>
            <totalSizeCap>1GB</totalSizeCap>
        </rollingPolicy>
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>

    <!-- 错误日志 -->
    <appender name="ERROR_FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>logs/agentscope-error.log</file>
        <filter class="ch.qos.logback.classic.filter.LevelFilter">
            <level>ERROR</level>
            <onMatch>ACCEPT</onMatch>
            <onMismatch>DENY</onMismatch>
        </filter>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>logs/agentscope-error.%d{yyyy-MM-dd}.%i.log</fileNamePattern>
            <maxFileSize>50MB</maxFileSize>
            <maxHistory>30</maxHistory>
        </rollingPolicy>
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>

    <root level="INFO">
        <appender-ref ref="CONSOLE"/>
        <appender-ref ref="FILE"/>
        <appender-ref ref="ERROR_FILE"/>
    </root>

    <!-- AgentScope专用日志 -->
    <logger name="com.brag.agentscope" level="DEBUG" additivity="false">
        <appender-ref ref="CONSOLE"/>
        <appender-ref ref="FILE"/>
    </logger>

    <!-- AgentScope框架日志 -->
    <logger name="io.agentscope" level="INFO" additivity="false">
        <appender-ref ref="CONSOLE"/>
        <appender-ref ref="FILE"/>
    </logger>
</configuration>
```

## 性能优化

### JVM调优

```bash
# 生产环境JVM参数
java -server \
  -Xms2g -Xmx4g \
  -XX:+UseG1GC \
  -XX:MaxGCPauseMillis=200 \
  -XX:+UseCompressedOops \
  -XX:+PrintGCDetails \
  -XX:+PrintGCTimeStamps \
  -jar app.jar
```

### 连接池配置

```yaml
# HTTP客户端连接池
agentscope:
  http:
    max-connections: 100
    max-connections-per-host: 20
    connection-timeout: 5000
    read-timeout: 30000
```

## 安全配置

### HTTPS配置

```yaml
server:
  ssl:
    key-store: classpath:keystore.p12
    key-store-password: password
    key-store-type: PKCS12
    key-alias: agentscope
  port: 8443
```

### API密钥管理

```yaml
# 使用外部配置中心
spring:
  cloud:
    config:
      uri: http://config-server:8888
      name: agentscope
      profile: production
```

### 防火墙配置

```bash
# 只开放必要端口
sudo ufw allow 8080/tcp
sudo ufw allow 22/tcp
sudo ufw --force enable
```

## 备份和恢复

### 数据备份

```bash
#!/bin/bash
# 日志备份脚本
DATE=$(date +%Y%m%d_%H%M%S)
tar -czf logs_backup_$DATE.tar.gz logs/
```

### 配置备份

```bash
#!/bin/bash
# 配置备份脚本
cp application.yml application.yml.backup
cp docker-compose.yml docker-compose.yml.backup
```

## 故障排除

### 常见问题

1. **端口占用**
```bash
# 检查端口占用
netstat -tulpn | grep 8080

# 杀死占用进程
kill -9 $(lsof -t -i:8080)
```

2. **内存不足**
```bash
# 增加JVM内存
export JAVA_OPTS="-Xms2g -Xmx4g"
```

3. **网络连接问题**
```bash
# 测试网络连接
curl -I https://dashscope.aliyuncs.com

# 检查DNS解析
nslookup dashscope.aliyuncs.com
```

### 诊断工具

```bash
# 查看Java进程
jps -l

# 查看JVM参数
jinfo <pid>

# 生成线程转储
jstack <pid> > thread_dump.txt

# 生成堆转储
jmap -dump:live,format=b,file=heap_dump.hprof <pid>
```

## 扩展部署

### 负载均衡

```nginx
# nginx.conf
upstream agentscope_backend {
    server 127.0.0.1:8080;
    server 127.0.0.1:8081;
    server 127.0.0.1:8082;
}

server {
    listen 80;
    server_name agentscope.example.com;

    location / {
        proxy_pass http://agentscope_backend;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
```

### 水平扩展

```bash
# 启动多个实例
docker-compose up --scale agentscope-app=3
```

### 监控集成

```yaml
# Prometheus配置
management:
  metrics:
    export:
      prometheus:
        enabled: true
  endpoint:
    metrics:
      enabled: true
    prometheus:
      enabled: true
```

这个部署指南涵盖了从开发环境到生产环境的完整部署流程。根据实际需求调整配置参数。


