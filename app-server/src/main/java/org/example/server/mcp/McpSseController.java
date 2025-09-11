package org.example.server.mcp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.function.Function;

@RestController
@RequestMapping("/mcp")
@CrossOrigin(origins = "*")
public class McpSseController {

    @Autowired
    private McpSseService mcpService;

    @Autowired
    private McpSseManager sseManager;

    // SSE连接端点
    @GetMapping(value = "/mcp", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter connect(@RequestParam(defaultValue = "client") String clientId) {
        return sseManager.createConnection(clientId);
    }

    // 接收客户端消息
    @PostMapping("/message")
    public ResponseEntity<Void> handleMessage(@RequestParam String clientId, @RequestBody McpMessage message) {
        McpMessage response = mcpService.handleMessage(clientId, message);
        sseManager.sendMessage(clientId, response);
        return ResponseEntity.ok().build();
    }

    // 动态添加工具
    @PostMapping("/add-tool")
    public String addTool(@RequestParam String name, @RequestParam String description) {
        Function<Map<String, Object>, Object> handler = params -> {
            return "执行工具: " + name + " 参数: " + params;
        };
        mcpService.addTool(name, description, handler);
        return "工具添加成功";
    }
}