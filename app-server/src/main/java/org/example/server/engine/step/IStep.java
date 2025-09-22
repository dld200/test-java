package org.example.server.engine.step;

import com.alibaba.fastjson.JSONObject;
import org.example.common.domain.TestStep;
import org.example.server.engine.ExecuteContext;

public interface IStep {

    String getType();

    Object execute(TestStep testStep, String params, ExecuteContext context);
}
