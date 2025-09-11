package org.example.server.mcp;

import lombok.Data;

@Data
public class McpError {
    private int code;
    private String message;
    private Object data;
}
