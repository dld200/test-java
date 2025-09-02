package org.example.server.executor;

import com.alibaba.fastjson.JSON;
import org.example.common.domain.TestStep;

public interface IStep {
     String getType();

      void execute(TestStep testStep, ExecutionContext context);
}
