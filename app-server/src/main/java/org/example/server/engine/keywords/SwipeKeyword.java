package org.example.server.engine.keywords;

import lombok.extern.slf4j.Slf4j;
import org.example.server.engine.Context;
import org.example.server.engine.Keyword;

@Slf4j
public class SwipeKeyword implements Keyword {
    @Override
    public String getName() {
        return "swipe";
    }

    @Override
    public Object execute(Context context, Object... args) {
        String direction = args[0].toString();
        log.info("Swipe : {}", direction);
        context.getAutomation().swipe(direction);
        return null;
    }
}