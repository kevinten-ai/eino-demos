package com.brag.agentscope.controller;

import com.brag.agentscope.service.AgentService;
import io.agentscope.core.message.Msg;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

/**
 * Agent REST控制器
 * 提供HTTP API接口用于与Agent交互
 */
@RestController
@RequestMapping("/api/agent")
@RequiredArgsConstructor
public class AgentController {

    private static final Logger log = LoggerFactory.getLogger(AgentController.class);

    private final AgentService agentService;

    /**
     * 基础对话接口
     */
    @PostMapping("/chat")
    public Mono<ResponseEntity<AgentResponse>> chat(@RequestBody ChatRequest request) {
        log.info("Received chat request: {}", request.getMessage());

        return agentService.chat(request.getMessage())
                .map(response -> ResponseEntity.ok(new AgentResponse(
                        response.getTextContent(),
                        "success")))
                .onErrorResume(error -> {
                    log.error("Chat processing error", error);
                    return Mono.just(ResponseEntity.internalServerError()
                            .body(new AgentResponse(null, "error: " + error.getMessage())));
                });
    }

    /**
     * 数学助手接口
     */
    @PostMapping("/math")
    public Mono<ResponseEntity<AgentResponse>> solveMath(@RequestBody ChatRequest request) {
        log.info("Received math request: {}", request.getMessage());

        return agentService.solveMath(request.getMessage())
                .map(response -> ResponseEntity.ok(new AgentResponse(
                        response.getTextContent(),
                        "success")))
                .onErrorResume(error -> {
                    log.error("Math processing error", error);
                    return Mono.just(ResponseEntity.internalServerError()
                            .body(new AgentResponse(null, "error: " + error.getMessage())));
                });
    }

    /**
     * 通用助手接口
     */
    @PostMapping("/general")
    public Mono<ResponseEntity<AgentResponse>> generalAssistant(@RequestBody ChatRequest request) {
        log.info("Received general assistant request: {}", request.getMessage());

        return agentService.generalAssistant(request.getMessage())
                .map(response -> ResponseEntity.ok(new AgentResponse(
                        response.getTextContent(),
                        "success")))
                .onErrorResume(error -> {
                    log.error("General assistant processing error", error);
                    return Mono.just(ResponseEntity.internalServerError()
                            .body(new AgentResponse(null, "error: " + error.getMessage())));
                });
    }

    /**
     * 获取可用工具信息
     */
    @GetMapping("/tools")
    public ResponseEntity<ToolsResponse> getAvailableTools() {
        String toolsInfo = agentService.getAvailableTools();
        String activatedGroups = agentService.getActivatedToolGroups();

        return ResponseEntity.ok(new ToolsResponse(toolsInfo, activatedGroups));
    }

    /**
     * 健康检查接口
     */
    @GetMapping("/health")
    public ResponseEntity<HealthResponse> health() {
        return ResponseEntity.ok(new HealthResponse("AgentScope AI Assistant is running", "UP"));
    }

    // 请求/响应数据类

    public static class ChatRequest {
        private String message;

        public ChatRequest() {}

        public ChatRequest(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

    public static class AgentResponse {
        private String response;
        private String status;

        public AgentResponse() {}

        public AgentResponse(String response, String status) {
            this.response = response;
            this.status = status;
        }

        public String getResponse() {
            return response;
        }

        public void setResponse(String response) {
            this.response = response;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }

    public static class ToolsResponse {
        private String availableTools;
        private String activatedGroups;

        public ToolsResponse() {}

        public ToolsResponse(String availableTools, String activatedGroups) {
            this.availableTools = availableTools;
            this.activatedGroups = activatedGroups;
        }

        public String getAvailableTools() {
            return availableTools;
        }

        public void setAvailableTools(String availableTools) {
            this.availableTools = availableTools;
        }

        public String getActivatedGroups() {
            return activatedGroups;
        }

        public void setActivatedGroups(String activatedGroups) {
            this.activatedGroups = activatedGroups;
        }
    }

    public static class HealthResponse {
        private String message;
        private String status;

        public HealthResponse() {}

        public HealthResponse(String message, String status) {
            this.message = message;
            this.status = status;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }
}



