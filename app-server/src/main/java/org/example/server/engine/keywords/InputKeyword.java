package org.example.server.engine.keywords;

import lombok.extern.slf4j.Slf4j;
import org.example.server.engine.Context;
import org.example.server.engine.Keyword;

@Slf4j
public class InputKeyword implements Keyword {
    @Override
    public String getName() {
        return "input";
    }

    @Override
    public Object execute(Context context, Object... args) {
        if (args.length < 2) {
            throw new IllegalArgumentException("input keyword requires element identifier and text");
        }
        String element = args[0].toString();
        String text = args[1].toString();
        log.info("Inputting '{}' into element: {}", text, element);
        context.getAutomation().input(element, text);
        return null;
    }
}