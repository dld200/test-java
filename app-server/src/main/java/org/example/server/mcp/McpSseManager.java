package org.example.server.mcp;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class McpSseManager {
    private final Map<String, SseEmitter> connections = new ConcurrentHashMap<>();
    
    public SseEmitter createConnection(String clientId) {
        SseEmitter emitter = new SseEmitter(0L); // 无超时
        
        emitter.onCompletion(() -> connections.remove(clientId));
        emitter.onTimeout(() -> connections.remove(clientId));
        emitter.onError(throwable -> connections.remove(clientId));
        
        connections.put(clientId, emitter);
        
        // 发送初始化消息
        sendInitMessage(emitter);
        
        return emitter;
    }
    
    private void sendInitMessage(SseEmitter emitter) {
        try {
            McpMessage initMsg = new McpMessage();
            initMsg.setMethod("notifications/initialized");
            initMsg.setParams(Map.of(
                "protocolVersion", "2024-11-05",
                "capabilities", Map.of(
                    "tools", Map.of("listChanged", true)
                )
            ));
            
            emitter.send(SseEmitter.event()
                .name("message")
                .data(initMsg));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void sendMessage(String clientId, McpMessage message) {
        SseEmitter emitter = connections.get(clientId);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event()
                    .name("message")
                    .data(message));
            } catch (Exception e) {
                connections.remove(clientId);
            }
        }
    }
    
    public void broadcast(McpNotification notification) {
        connections.forEach((clientId, emitter) -> {
            try {
                emitter.send(SseEmitter.event()
                    .name("notification")
                    .data(notification));
            } catch (Exception e) {
                connections.remove(clientId);
            }
        });
    }
}