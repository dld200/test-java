package org.example.server.mcp;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Component
public class ToolRegistry {
    private final Map<String, SimpleTool> tools = new HashMap<>();
    
    public void addTool(String name, String description, Function<Map<String, Object>, Object> handler) {
        SimpleTool tool = new SimpleTool();
        tool.setName(name);
        tool.setDescription(description);
        tool.setHandler(handler);
        tools.put(name, tool);
    }
    
    public Object executeTool(String name, Map<String, Object> params) {
        SimpleTool tool = tools.get(name);
        return tool != null ? tool.getHandler().apply(params) : "工具不存在";
    }
    
    public List<SimpleTool> getAllTools() {
        return new ArrayList<>(tools.values());
    }
}