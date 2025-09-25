package org.example.server.engine.step.script.keyword;

import lombok.extern.slf4j.Slf4j;
import org.example.mobile.automation.Automation;
import org.example.server.engine.MobileContext;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SwipeKeyword implements Keyword {
    @Override
    public String getName() {
        return "swipe";
    }

    @Override
    public Object execute(MobileContext context, Object... args) {
        if (args.length != 1) {
            throw new IllegalArgumentException();
        }
        String direction = args[0].toString();
        return context.getAutomation().swipe(direction);
    }
}