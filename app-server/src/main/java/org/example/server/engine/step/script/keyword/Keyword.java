package org.example.server.engine.step.script.keyword;

import org.example.server.engine.MobileContext;

public interface Keyword {

    String getName();

    Object execute(MobileContext context, Object... args);
}