package org.example.server.engine.step.script.keyword;

import lombok.extern.slf4j.Slf4j;
import org.example.mobile.automation.Automation;
import org.example.server.engine.MobileContext;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class InputKeyword implements Keyword {
    @Override
    public String getName() {
        return "input";
    }

    @Override
    public Object execute(MobileContext context, Object... args) {
        if (args.length != 2) {
            throw new IllegalArgumentException();
        }
        String element = args[0].toString();
        String text = args[1].toString();
        return context.getAutomation().input(element, text);
    }
}