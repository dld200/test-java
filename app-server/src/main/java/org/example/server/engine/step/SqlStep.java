package org.example.server.engine.step;

import com.alibaba.fastjson.JSON;
import lombok.Setter;
import org.example.common.domain.TestStep;
import org.example.server.engine.ExecuteContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Setter
@Component
@Scope("prototype")
public class SqlStep implements IStep {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public String getType() {
        return "sql";
    }

    @Override
    public Object execute(TestStep testStep, String params, ExecuteContext context) {
        String database = JSON.parseObject(params).getString("database");
        String sql = JSON.parseObject(params).getString("sql");
        sql = sql.trim().toLowerCase();
        try {
            if (sql.startsWith("select")) {
                // 查询语句
                List<Map<String, Object>> results = jdbcTemplate.queryForList(sql);
                return results;
            } else if (sql.startsWith("insert") || sql.startsWith("update") || sql.startsWith("delete")) {
                // 更新语句
                int rows = jdbcTemplate.update(sql);
                return rows;
            } else {
                // DDL 或其他语句
                jdbcTemplate.execute(sql);
                return "Executed successfully";
            }
        } catch (Exception e) {
            // 捕获异常，返回错误信息
            return "Error: " + e.getMessage();
        }
    }
}
