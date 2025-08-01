package org.example.server.event;

import com.google.common.eventbus.EventBus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EventPublisherService {
    
    @Autowired
    private EventBus eventBus;
    
    /**
     * 发布一个DemoEvent事件
     * @param message 事件消息
     */
    public void publishDemoEvent(String message) {
        DemoEvent event = new DemoEvent(message);
        log.info("Publishing DemoEvent: {}", event.getMessage());
        eventBus.post(event);
    }
    
    /**
     * 发布自定义ID和时间戳的事件
     * @param id 事件ID
     * @param message 事件消息
     * @param timestamp 时间戳
     */
    public void publishCustomDemoEvent(String id, String message, long timestamp) {
        DemoEvent event = new DemoEvent(id, message, timestamp);
        log.info("Publishing custom DemoEvent: ID={}, Message={}", id, message);
        eventBus.post(event);
    }
}