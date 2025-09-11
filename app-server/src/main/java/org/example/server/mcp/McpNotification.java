package org.example.server.mcp;

import lombok.Data;

@Data
public class McpNotification {
    private String jsonrpc = "2.0";
    private String method;
    private Object params;
}