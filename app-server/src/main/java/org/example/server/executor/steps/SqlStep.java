package org.example.server.executor.steps;

import lombok.Data;
import org.example.common.domain.TestStep;
import org.example.server.executor.ExecutionContext;
import org.example.server.executor.IStep;
import org.example.server.executor.StepFactory;

@Data
public class SqlStep implements IStep {
    private String database;
    private String sql;

    @Override
    public String getType() {
        return "sql";
    }

    @Override
    public void execute(TestStep testStep, ExecutionContext context) {
        String resolvedSql = context.replaceVariables(sql);
        String result = "Executed SQL: " + resolvedSql + " on database: " + database;
        context.addStepResult(testStep.getId(), result);
    }

    // 静态代码块自动注册
    static {
        StepFactory.registerStep("sql", SqlStep.class);
    }
}
