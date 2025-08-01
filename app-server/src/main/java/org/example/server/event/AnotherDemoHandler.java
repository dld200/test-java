package org.example.server.event;

import com.google.common.eventbus.Subscribe;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AnotherDemoHandler {
    
    @Subscribe
    public void handleDemoEvent(DemoEvent event) {
        log.info("AnotherDemoHandler received DemoEvent - ID: {}, Message: {}", 
                event.getId(), event.getMessage());
        
        // 模拟不同的处理逻辑
        performAdditionalProcessing(event);
    }
    
    private void performAdditionalProcessing(DemoEvent event) {
        // 模拟一些额外的处理
        log.info("Performing additional processing for event: {}", event.getId());
        
        try {
            // 模拟处理耗时
            Thread.sleep(50);
            log.info("Additional processing completed for event: {}", event.getId());
        } catch (InterruptedException e) {
            log.error("Interrupted during additional processing", e);
            Thread.currentThread().interrupt();
        }
    }
}