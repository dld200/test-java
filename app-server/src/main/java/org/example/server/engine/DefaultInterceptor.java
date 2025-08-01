package org.example.server.engine;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DefaultInterceptor implements Interceptor {

    @Override
    public void beforeStatement(Context context, String statement) {
        // 检查是否需要在语句执行前添加延迟
        Object sleepBefore = context.getOption("sleep.before.statement");
        if (sleepBefore != null && Boolean.parseBoolean(sleepBefore.toString())) {
            sleep(context, "before.statement.delay");
        }
    }

    @Override
    public void afterStatement(Context context, String statement) {
        // 检查是否需要在语句执行后添加延迟
        Object sleepAfter = context.getOption("sleep.after.statement");
        if (sleepAfter != null && Boolean.parseBoolean(sleepAfter.toString())) {
            sleep(context, "after.statement.delay");
        }

        // 检查是否需要截图
        Object screenshotEnabled = context.getOption("screenshot.after.statement");
        if (screenshotEnabled != null && Boolean.parseBoolean(screenshotEnabled.toString())) {
            screenshot(context, statement);
        }
    }

    @Override
    public void beforeExecution(Context context) {
        // 在测试用例执行前的操作
        Object sleepBefore = context.getOption("sleep.before.execution");
        if (sleepBefore != null && Boolean.parseBoolean(sleepBefore.toString())) {
            sleep(context, "before.execution.delay");
        }
    }

    @Override
    public void afterExecution(Context context) {
        // 在测试用例执行后的操作
//        Object screenshotEnabled = context.getOption("screenshot.after.execution");
//        if (screenshotEnabled != null && Boolean.parseBoolean(screenshotEnabled.toString())) {
//            screenshot(context, "final");
//        }
    }

    private void sleep(Context context, String delayKey) {
        Object delayObj = context.getOption(delayKey);
        long delay = 1000; // 默认延迟1秒
        if (delayObj != null) {
            try {
                delay = Long.parseLong(delayObj.toString());
            } catch (NumberFormatException e) {
                // 使用默认延迟
            }
        }
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void screenshot(Context context, String name) {
        log.info("Taking screenshot: {}", name);
        // 这里应该是实际的截图实现
        // 例如调用截图工具或WebDriver的截图功能
    }
}