package org.example.server.engine.step;

import lombok.Data;
import org.example.common.domain.TestStep;
import org.example.server.engine.ExecuteContext;
import org.example.server.engine.StepFactory;

@Data
public class GroovyStep implements IStep {
    private String database;
    private String sql;

    @Override
    public String getType() {
        return "sql";
    }

    @Override
    public void execute(TestStep testStep, ExecuteContext context) {
        String resolvedSql = context.replaceVariables(sql);
        String result = "Executed SQL: " + resolvedSql + " on database: " + database;
        context.addStepResult(testStep.getId(), result);
    }

    // 静态代码块自动注册
    static {
        StepFactory.registerStep("sql", GroovyStep.class);
    }
}
