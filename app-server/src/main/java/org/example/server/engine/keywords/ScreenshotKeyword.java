package org.example.server.engine.keywords;

import lombok.extern.slf4j.Slf4j;
import org.example.server.engine.Context;
import org.example.server.engine.Keyword;

@Slf4j
public class ScreenshotKeyword implements Keyword {
    @Override
    public String getName() {
        return "screenshot";
    }

    @Override
    public Object execute(Context context, Object... args) {
        String name = "screenshot";
        if (args.length > 0) {
            name = args[0].toString();
        }
        log.info("Taking screenshot: {}", name);
        context.getAutomation().screenshot(name);
        return null;
    }
}