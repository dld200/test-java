package org.example.server.engine.step.script.keyword;

import lombok.extern.slf4j.Slf4j;
import org.example.mobile.automation.Automation;

@Slf4j
public class ClickKeyword implements Keyword {
    @Override
    public String getName() {
        return "click";
    }

    @Override
    public Object execute(Automation automation, Object... args) {
        String elementId = args[0].toString();
        log.info("Clicking element: {}", elementId);
        return automation.click(elementId);
    }
}