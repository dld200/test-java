package org.example.server.mcp;

import io.modelcontextprotocol.server.McpServer;
import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.server.McpSyncServer;
import io.modelcontextprotocol.server.transport.WebMvcStreamableServerTransportProvider;
import io.modelcontextprotocol.spec.McpSchema;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.example.server.mcp.function.McpDemoFunction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class McpService {

    @Autowired
    private WebMvcStreamableServerTransportProvider transportProvider;

    private McpSyncServer syncServer;

    @PostConstruct
    public void init() {
        this.syncServer = start();
        addTool(new McpDemoFunction());
    }

    @PreDestroy
    public void stop() {
        if (this.syncServer != null) {
            log.info("Stopping mcp server...");
            this.syncServer.closeGracefully();
        }
    }

    public McpSyncServer start() {
        log.info("Starting mcp server...");
        McpSyncServer syncServer = McpServer.sync(transportProvider)
                .serverInfo("my-server", "1.0.0")
                .capabilities(McpSchema.ServerCapabilities.builder()
//                        .resources(false, true)
                        .tools(true)
//                        .prompts(true)
                        .logging()
                        .completions()
                        .build())
                .build();
        return syncServer;
    }

    public void addTool(McpDemoFunction function) {
        try {
            var createMoonTool = new McpServerFeatures.SyncToolSpecification(
                    new McpSchema.Tool(function.getName(), function.getDesc(), function.getSchema()),
                    (exchange, arguments) -> function.apply(arguments)
            );
            syncServer.addTool(createMoonTool);
            log.info("Successfully registered tool");
        } catch (Exception e) {
            log.error("Failed to register tool", e);
        }
    }

    public void remoteTool(String toolName) {
        if (syncServer != null) {
            try {
                syncServer.removeTool(toolName);
                log.info("Successfully removed tool");
            } catch (Exception e) {
                log.error("Failed to remove tool", e);
            }
        }
    }
}
