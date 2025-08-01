package org.example.server.event;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DemoHandler {
    
    public void handleDemoEvent(DemoEvent event) {
        log.info("Handling DemoEvent - ID: {}, Message: {}, Timestamp: {}", 
                event.getId(), event.getMessage(), event.getTimestamp());
        
        // 模拟处理逻辑
        processEvent(event);
    }
    
    private void processEvent(DemoEvent event) {
        // 模拟一些处理时间
        try {
            Thread.sleep(100);
            log.info("Processed DemoEvent with message: {}", event.getMessage());
        } catch (InterruptedException e) {
            log.error("Interrupted while processing event", e);
            Thread.currentThread().interrupt();
        }
    }
}