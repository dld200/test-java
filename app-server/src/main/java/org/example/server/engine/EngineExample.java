//package org.example.biz.engine;
//
//import org.example.domain.TestCase;
//import lombok.extern.slf4j.Slf4j;
//
//import java.util.HashMap;
//import java.util.Map;
//
//@Slf4j
//public class EngineExample {
//    public static void main(String[] args) {
//        // 创建测试用例
//        String sampleDsl = "# Sample test case\n" +
//                "navigate 'https://example.com'\n" +
//                "click 'loginButton'\n" +
//                "input 'username', 'testuser'\n" +
//                "input 'password', 'password123'\n" +
//                "click 'submit'\n" +
//                "wait 2000\n" +
//                "assert ctx.getVariable('currentUrl'), 'https://example.com/dashboard'";
//
//        sampleDsl = "log 'xxxx'";
//
//        TestCase testCase = new TestCase(1L, "Login Test", sampleDsl);
//
//        // 创建执行器
//        Executor executor = new Executor();
//
//        // 添加拦截器
//        executor.addInterceptor(new DefaultInterceptor());
//
//        // 设置执行选项
//        Map<String, Object> options = new HashMap<>();
//        options.put("sleep.after.statement", true);
//        options.put("after.statement.delay", 500);
//        options.put("screenshot.after.statement", true);
//
//        // 执行测试用例
//        Context context = new Context(testCase);
//        context.setOptions(options);
//
//        try {
//            Context resultContext = executor.execute(context);
//            log.info("Test execution completed. Result: {}", resultContext.getResult());
//        } catch (Exception e) {
//            log.error("Test execution failed: {}", e.getMessage(), e);
//        }
//    }
//}