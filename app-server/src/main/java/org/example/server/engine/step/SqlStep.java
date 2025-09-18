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

    private String sql;

    @Override
    public String getName() {
        return "sql";
    }

    @Override
    public String execute(TestStep testStep, ExecuteContext context) {
        String result = "Executed SQL: " + sql + " on database: " + database;
        context.setStepResult(testStep.getId(), result);
        return result;
    }

    // 静态代码块自动注册
    static {
        StepFactory.registerStep("sql", SqlStep.class);
    }
}
