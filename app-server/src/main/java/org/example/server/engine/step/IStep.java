package org.example.server.engine.step;

import org.example.common.domain.TestStep;
import org.example.server.engine.ExecuteContext;

public interface IStep {

     String getType();

      void execute(TestStep testStep, ExecuteContext context);
}
