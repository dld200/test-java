package org.example.server.engine;

import lombok.extern.slf4j.Slf4j;
import org.example.common.domain.TestCase;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class ComprehensiveExample {
    public static void main(String[] args) {
        // 创建测试用例，使用更自然的DSL语法
        String sampleDsl = "# Comprehensive test case\n" +
                "log('Starting test execution')\n" +
                "navigate('https://example.com')\n" +
                "screenshot('homepage')\n" +
                "click('loginButton')\n" +
                "input('username', 'testuser')\n" +
                "input('password', 'password123')\n" +
                "screenshot('before-submit')\n" +
                "click('submit')\n" +
                "wait(2000)\n" +
                "screenshot('after-submit')\n" +
                "log('Test execution completed')\n" +
                "assert(ctx.getVariable('currentUrl'), 'https://example.com/dashboard')";

        TestCase testCase = TestCase.builder().dsl(sampleDsl).build();

        // 创建执行器
        Executor executor = new Executor();

        // 添加拦截器
        executor.addInterceptor(new DefaultInterceptor());

        // 设置执行选项
        Map<String, Object> options = new HashMap<>();
        options.put("sleep.after.statement", true);
        options.put("after.statement.delay", 300);

        // 执行测试用例
        Context context = new Context(testCase);
        context.setOptions(options);

        try {
            Context resultContext = executor.execute(context);
            log.info("Test execution completed. Result: {}", resultContext.getResult());
            
            // 输出执行过程中设置的变量
            log.info("\\nExecution context variables:");
            resultContext.getVariables().forEach((key, value) -> 
                log.info("  {} = {}", key, value));
        } catch (Exception e) {
            log.error("Test execution failed: {}", e.getMessage(), e);
        }
    }
}