package org.example.server.engine;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Slf4j
public class DefaultInterceptor implements Interceptor {

    @Override
    public void beforeExecution(Context context) {
        Object sleepBefore = context.getOption("sleep.before.execution");
        if (sleepBefore != null && Boolean.parseBoolean(sleepBefore.toString())) {
            sleep(context, "before.execution.delay");
        }
    }

    @Override
    public void afterExecution(Context context) {
        Object sleepBefore = context.getOption("sleep.before.execution");
        if (sleepBefore != null && Boolean.parseBoolean(sleepBefore.toString())) {
            sleep(context, "after.execution.delay");
        }
        Object screenshotEnabled = context.getOption("screenshot.after.execution");
        if (screenshotEnabled != null && Boolean.parseBoolean(screenshotEnabled.toString())) {
            screenshot(context, "final.png");
        }
    }

    @Override
    public void beforeKeyword(Context context, String keyword) {
        Object sleepBefore = context.getOption("sleep.before.keyword");
        if (sleepBefore != null && Integer.parseInt(sleepBefore.toString()) > 0) {
            sleep(context, "sleep.before.keyword");
        }
    }

    @Override
    public void afterKeyword(Context context, String keyword) {
        Object screenshotEnabled = context.getOption("screenshot.after.keyword");
        if (screenshotEnabled != null && Boolean.parseBoolean(screenshotEnabled.toString())) {
            screenshot(context, keyword + ".png");
        }
        Object sleepBefore = context.getOption("sleep.after.keyword");
        if (sleepBefore != null && Integer.parseInt(sleepBefore.toString()) > 0) {
            sleep(context, "sleep.after.keyword");
        }
    }

    private void sleep(Context context, String delayKey) {
        Object delayObj = context.getOption(delayKey);
        long delay = 1000; // 默认延迟1秒
        if (delayObj != null) {
            delay = Long.parseLong(delayObj.toString());
        }
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            log.error("Error while sleeping", e);
        }
    }

    private void screenshot(Context context, String fileName) {
//        fileName = "screenshot_" + LocalDateTime.now() + ".png";
        context.getAutomation().screenshot(fileName);
    }
}