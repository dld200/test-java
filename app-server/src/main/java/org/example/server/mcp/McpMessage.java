package org.example.server.mcp;

import lombok.Data;

@Data
public class McpMessage {
    private String jsonrpc = "2.0";
    private String method;
    private Object params;
    private String id;
    private Object result;
    private McpError error;
}

