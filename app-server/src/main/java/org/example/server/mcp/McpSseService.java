package org.example.server.mcp;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class McpSseService {
    
    @Autowired
    private ToolRegistry toolRegistry;
    
    @Autowired
    private McpSseManager sseManager;
    
    @PostConstruct
    public void init() {
        // 注册示例工具
        toolRegistry.addTool("calculator", "计算器", this::calculate);
        toolRegistry.addTool("weather", "天气查询", this::getWeather);
    }
    
    public McpMessage handleMessage(String clientId, McpMessage request) {
        McpMessage response = new McpMessage();
        response.setId(request.getId());
        
        try {
            switch (request.getMethod()) {
                case "initialize":
                    response.setResult(Map.of(
                        "protocolVersion", "2024-11-05",
                        "capabilities", Map.of(
                            "tools", Map.of()
                        ),
                        "serverInfo", Map.of(
                            "name", "Spring AI MCP Server",
                            "version", "1.0.0"
                        )
                    ));
                    break;
                    
                case "tools/list":
                    response.setResult(Map.of("tools", getToolsList()));
                    break;
                    
                case "tools/call":
                    Map<String, Object> params = (Map<String, Object>) request.getParams();
                    String name = (String) params.get("name");
                    Map<String, Object> arguments = (Map<String, Object>) params.get("arguments");
                    
                    Object result = toolRegistry.executeTool(name, arguments);
                    response.setResult(Map.of(
                        "content", List.of(Map.of(
                            "type", "text",
                            "text", result.toString()
                        ))
                    ));
                    break;
                    
                default:
                    McpError error = new McpError();
                    error.setCode(-32601);
                    error.setMessage("Method not found");
                    response.setError(error);
            }
        } catch (Exception e) {
            McpError error = new McpError();
            error.setCode(-32603);
            error.setMessage("Internal error: " + e.getMessage());
            response.setError(error);
        }
        
        return response;
    }
    
    private List<Map<String, Object>> getToolsList() {
        return toolRegistry.getAllTools().stream()
            .map(tool -> Map.of(
                "name", tool.getName(),
                "description", tool.getDescription(),
                "inputSchema", Map.of(
                    "type", "object",
                    "properties", Map.of(
                        "input", Map.of("type", "string", "description", "输入参数")
                    )
                )
            ))
            .collect(Collectors.toList());
    }
    
    public void addTool(String name, String description, Function<Map<String, Object>, Object> handler) {
        toolRegistry.addTool(name, description, handler);
        
        // 广播工具列表变更通知
        McpNotification notification = new McpNotification();
        notification.setMethod("notifications/tools/list_changed");
        sseManager.broadcast(notification);
    }
    
    // 示例工具
    private Object calculate(Map<String, Object> params) {
        String input = (String) params.get("input");
        return "计算结果: " + input;
    }
    
    private Object getWeather(Map<String, Object> params) {
        String location = (String) params.get("input");
        return "天气: " + location + " 晴天 25度";
    }
}