package org.example.server.engine;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.example.common.domain.TestCase;
import org.example.common.domain.TestStep;
import org.example.server.engine.step.IStep;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class ExecuteService {

    public void execute(TestCase testCase) {
        // 创建执行上下文
        ExecuteContext context = new ExecuteContext();
        Map<String, String> variables = JSON.parseObject(testCase.getConfig(), new TypeReference<>() {
        });

        // 将TestCase的variables放入执行上下文
//        if (testCase.getConfig() != null) {
//            context.getVariables().putAll(variables);
//        }

        // 获取TestSteps
        for (TestStep testStep : testCase.getSteps()) {
            try {
                IStep step = StepFactory.createStep(testStep.getType(), context.replaceVariables(testStep.getConfig()), context);
                step.execute(testStep, context);
//                context.setVariable("");
            } catch (Exception e) {
                context.addStepResult(testStep.getId(), "Error: " + e.getMessage());
            }
        }
    }
}