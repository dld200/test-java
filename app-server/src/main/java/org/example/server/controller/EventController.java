package org.example.server.controller;

import lombok.extern.slf4j.Slf4j;
import org.example.server.event.EventPublisherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/events")
public class EventController {
    
    @Autowired
    private EventPublisherService eventPublisherService;
    
    @PostMapping("/demo")
    public String publishDemoEvent(@RequestParam String message) {
        log.info("Received request to publish demo event with message: {}", message);
        eventPublisherService.publishDemoEvent(message);
        return "Demo event published successfully";
    }
    
    @PostMapping("/custom")
    public String publishCustomDemoEvent(@RequestParam String id, 
                                       @RequestParam String message, 
                                       @RequestParam long timestamp) {
        log.info("Received request to publish custom demo event: ID={}, Message={}, Timestamp={}", 
                id, message, timestamp);
        eventPublisherService.publishCustomDemoEvent(id, message, timestamp);
        return "Custom demo event published successfully";
    }
}