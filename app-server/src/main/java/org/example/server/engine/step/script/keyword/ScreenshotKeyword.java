package org.example.server.engine.step.script.keyword;

import lombok.extern.slf4j.Slf4j;
import org.example.mobile.automation.Automation;

@Slf4j
public class ScreenshotKeyword implements Keyword {
    @Override
    public String getName() {
        return "screenshot";
    }

    @Override
    public Object execute(Automation automation, Object... args) {
        String name = "screenshot";
        if (args.length > 0) {
            name = args[0].toString();
        }
        log.info("Taking screenshot: {}", name);
        return automation.screenshot(name);
    }
}