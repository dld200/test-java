package org.example.server.engine.groovy.keyword;

import lombok.extern.slf4j.Slf4j;
import org.example.server.engine.groovy.MobileContext;
import org.example.server.engine.groovy.Keyword;

@Slf4j
public class ScreenshotKeyword implements Keyword {
    @Override
    public String getName() {
        return "screenshot";
    }

    @Override
    public Object execute(MobileContext mobileContext, Object... args) {
        String name = "screenshot";
        if (args.length > 0) {
            name = args[0].toString();
        }
        log.info("Taking screenshot: {}", name);
        mobileContext.getAutomation().screenshot(name);
        return null;
    }
}