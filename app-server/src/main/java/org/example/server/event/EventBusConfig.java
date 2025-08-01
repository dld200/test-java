package org.example.server.event;

import com.google.common.eventbus.EventBus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EventBusConfig {
    
    @Bean
    public EventBus eventBus() {
        return new EventBus("AppAgentEventBus");
    }
    
    @Bean
    public DemoHandler demoHandler(EventBus eventBus) {
        DemoHandler demoHandler = new DemoHandler();
        eventBus.register(demoHandler);
        return demoHandler;
    }
    
    @Bean
    public AnotherDemoHandler anotherDemoHandler(EventBus eventBus) {
        AnotherDemoHandler anotherDemoHandler = new AnotherDemoHandler();
        eventBus.register(anotherDemoHandler);
        return anotherDemoHandler;
    }
}