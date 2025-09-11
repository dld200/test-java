package org.example.server.mcp;

import lombok.Data;

import java.util.Map;
import java.util.function.Function;

@Data
public class SimpleTool {
    private String name;
    private String description;
    private Function<Map<String, Object>, Object> handler;
}