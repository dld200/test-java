package org.example.server.mcp.function;


import io.modelcontextprotocol.spec.McpSchema;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.function.Function;


@Data
@Slf4j
public class McpDemoFunction implements Function<Map<String, Object>, McpSchema.CallToolResult> {

    private String name = "create_task_executor";

    private String desc = "Help user to create task";

    private String schema = """
            {
                "type": "object",
                "properties": {
                    "name": {
                        "type": "string",
                        "description": "任务名"
                    }
                },
                "required": ["name"]
            }
            """;

    @SneakyThrows
    @Override
    public McpSchema.CallToolResult apply(Map<String, Object> args) {
        try {
            // 1. 参数验证和转换
            if (args == null || args.isEmpty()) {
                throw new IllegalArgumentException("Task parameters are required");
            }

            if (args.get("name") == null || ((String) args.get("name")).trim().isEmpty()) {
                throw new IllegalArgumentException("task name is required and cannot be empty");
            }

            return new McpSchema.CallToolResult(
                    List.of(new McpSchema.TextContent("Task created successfully." + args.get("name"))),
                    false
            );

        } catch (Exception ex) {
            log.error("Failed to create task", ex);
            throw new RuntimeException("Failed to create task: " + ex.getMessage());
        }
    }
}