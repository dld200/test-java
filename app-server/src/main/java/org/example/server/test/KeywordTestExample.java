package org.example.server.test;

import org.example.common.domain.TestCase;
import org.example.mobile.device.impl.IosSimulatorAutomation;
import org.example.server.engine.groovy.MobileContext;
import org.example.server.engine.groovy.DefaultInterceptor;
import org.example.server.engine.groovy.Executor;

import java.util.HashMap;
import java.util.Map;

public class KeywordTestExample {
    public static void main(String[] args) {
        // 创建测试用例，使用更自然的DSL语法
        String sampleDsl = "# Sample test case with direct keyword calls\n" +
                "setup('F0F99D79-FCB0-45C3-AD55-89CCCA9BDBFD', 'ca.snappay.snaplii.test')\n" +
                "click('Search from over 200 top brands')\n" +
                "input('Enter a brand name', 'amazon')\n" +
                "# screenshot('xx.png')\n" +
                "swipe('left')\n" +
                "click('Log in/Sign up')";

        TestCase testCase = TestCase.builder().id(2L).script(sampleDsl).build();

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
        MobileContext mobileContext = new MobileContext();
        mobileContext.setOptions(options);
        mobileContext.setAutomation(new IosSimulatorAutomation());

        try {
            MobileContext resultMobileContext = executor.execute(mobileContext);
            System.out.println("Test execution completed. Result: " + resultMobileContext.getResult());
        } catch (Exception e) {
            System.err.println("Test execution failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}