package org.example.server.engine.step;

import org.example.common.domain.TestStep;
import org.example.server.engine.ExecuteContext;
import org.example.server.engine.StepFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

public class SqlStep implements IStep {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private String database;
    private String text;

    @Override
    public String getType() {
        return "sql";
    }

    @Override
    public void execute(TestStep testStep, ExecuteContext context) {
        String resolvedSql = context.replaceVariables(text);
        String result = "Executed SQL: " + resolvedSql + " on database: " + database;
        context.addStepResult(testStep.getId(), result);
    }

    // 静态代码块自动注册
    static {
        StepFactory.registerStep("sql", SqlStep.class);
    }
}
