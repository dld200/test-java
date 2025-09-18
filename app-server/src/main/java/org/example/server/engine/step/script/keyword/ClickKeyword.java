package org.example.server.engine.step.script.keyword;

import lombok.extern.slf4j.Slf4j;
import org.example.mobile.automation.Automation;
import org.example.server.engine.MobileContext;

@Slf4j
public class ClickKeyword implements Keyword {
    @Override
    public String getName() {
        return "click";
    }

    @Override
    public Object execute(MobileContext context, Object... args) {
        if (args.length != 1) {
            throw new IllegalArgumentException();
        }
        String elementId = args[0].toString();
        return context.getAutomation().click(elementId);
    }
}