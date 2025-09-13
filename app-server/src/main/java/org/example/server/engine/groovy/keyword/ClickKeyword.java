package org.example.server.engine.groovy.keyword;

import lombok.extern.slf4j.Slf4j;
import org.example.server.engine.groovy.MobileContext;
import org.example.server.engine.groovy.Keyword;

@Slf4j
public class ClickKeyword implements Keyword {
    @Override
    public String getName() {
        return "click";
    }

    @Override
    public Object execute(MobileContext mobileContext, Object... args) {
        String elementId = args[0].toString();
        log.info("Clicking element: {}", elementId);
        mobileContext.getAutomation().click(elementId);
        return null;
    }
}