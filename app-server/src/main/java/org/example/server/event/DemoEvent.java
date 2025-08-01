package org.example.server.event;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DemoEvent {
    private String id;
    private String message;
    private long timestamp;
    
    public DemoEvent(String message) {
        this.id = java.util.UUID.randomUUID().toString();
        this.message = message;
        this.timestamp = System.currentTimeMillis();
    }
}