package org.example.server.engine.step.script.keyword;

import lombok.extern.slf4j.Slf4j;
import org.example.mobile.automation.Automation;
import org.example.server.engine.MobileContext;

@Slf4j
public class ScreenshotKeyword implements Keyword {
    @Override
    public String getName() {
        return "screenshot";
    }

    @Override
    public Object execute(MobileContext context, Object... args) {
        String name = "screenshot";
        if (args.length > 0) {
            name = args[0].toString();
        }
        return context.getAutomation().screenshot(name);
    }
}