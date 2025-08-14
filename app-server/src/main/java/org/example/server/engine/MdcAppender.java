package org.example.server.engine;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MdcAppender extends AppenderBase<ILoggingEvent> {

    private final List<ILoggingEvent> events = Collections.synchronizedList(new ArrayList<>());

    @Override
    protected void append(ILoggingEvent event) {
        String flag = event.getMDCPropertyMap().get("logToMemory");
        if ("true".equals(flag)) {
            events.add(event);
        }
    }

    public List<ILoggingEvent> getEvents() {
        return events;
    }

    public void clear() {
        events.clear();
    }
}
