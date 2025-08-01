package org.example.server.engine;

import org.example.common.domain.TestCase;

import java.util.HashMap;
import java.util.Map;

public class KeywordTestExample {
    public static void main(String[] args) {
        // 创建测试用例，使用更自然的DSL语法
        String sampleDsl = "# Sample test case with direct keyword calls\n" +
                "navigate('https://example.com')\n" +
                "click('loginButton')\n" +
                "input('username', 'testuser')\n" +
                "input('password', 'password123')\n" +
                "click('submit')\n" +
                "wait(2000)\n" +
                "assert(ctx.getVariable('currentUrl'), 'https://example.com/dashboard')";

        TestCase testCase = TestCase.builder().id(2L).dsl(sampleDsl).build();

        // 创建执行器
        Executor executor = new Executor();

        // 添加拦截器
        executor.addInterceptor(new DefaultInterceptor());

        // 设置执行选项
        Map<String, Object> options = new HashMap<>();
        options.put("sleep.after.statement", true);
        options.put("after.statement.delay", 500);
        options.put("screenshot.after.statement", true);

        // 执行测试用例
        Context context = new Context(testCase);
        context.setOptions(options);

        try {
            Context resultContext = executor.execute(context);
            System.out.println("Test execution completed. Result: " + resultContext.getResult());
        } catch (Exception e) {
            System.err.println("Test execution failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}