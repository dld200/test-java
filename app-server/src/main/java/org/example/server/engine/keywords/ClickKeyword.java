package org.example.server.engine.keywords;

import lombok.extern.slf4j.Slf4j;
import org.example.server.engine.Context;
import org.example.server.engine.Keyword;

@Slf4j
public class ClickKeyword implements Keyword {
    @Override
    public String getName() {
        return "click";
    }

    @Override
    public Object execute(Context context, Object... args) {
        String elementId = args[0].toString();
        log.info("Clicking element: {}", elementId);
        context.getAutomation().click(elementId);
        return null;
    }
}