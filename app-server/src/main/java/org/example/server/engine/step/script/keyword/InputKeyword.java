package org.example.server.engine.step.script.keyword;

import lombok.extern.slf4j.Slf4j;
import org.example.mobile.automation.Automation;

@Slf4j
public class InputKeyword implements Keyword {
    @Override
    public String getName() {
        return "input";
    }

    @Override
    public Object execute(Automation automation, Object... args) {
        if (args.length < 2) {
            throw new IllegalArgumentException("input keyword requires element identifier and text");
        }
        String element = args[0].toString();
        String text = args[1].toString();
        log.info("Inputting '{}' into element: {}", text, element);
        return automation.input(element, text);
    }
}