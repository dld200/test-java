package org.example.server.engine.step.script.keyword;

import lombok.extern.slf4j.Slf4j;
import org.example.mobile.automation.Automation;

@Slf4j
public class SwipeKeyword implements Keyword {
    @Override
    public String getName() {
        return "swipe";
    }

    @Override
    public Object execute(Automation automation, Object... args) {
        String direction = args[0].toString();
        log.info("Swipe : {}", direction);
        return automation.swipe(direction);
    }
}