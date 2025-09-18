package org.example.server.engine.step;

import org.example.common.domain.TestStep;
import org.example.server.engine.ExecuteContext;

public interface IStep {

    String getName();

    String execute(TestStep testStep, ExecuteContext context);
}
