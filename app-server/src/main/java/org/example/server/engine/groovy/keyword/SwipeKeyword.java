package org.example.server.engine.groovy.keyword;

import lombok.extern.slf4j.Slf4j;
import org.example.server.engine.groovy.MobileContext;
import org.example.server.engine.groovy.Keyword;

@Slf4j
public class SwipeKeyword implements Keyword {
    @Override
    public String getName() {
        return "swipe";
    }

    @Override
    public Object execute(MobileContext mobileContext, Object... args) {
        String direction = args[0].toString();
        log.info("Swipe : {}", direction);
        mobileContext.getAutomation().swipe(direction);
        return null;
    }
}