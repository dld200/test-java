package org.example.server.engine.groovy;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DefaultInterceptor implements Interceptor {

    @Override
    public void beforeExecution(MobileContext mobileContext) {
        Object sleepBefore = mobileContext.getOption("sleep.before.execution");
        if (sleepBefore != null && Boolean.parseBoolean(sleepBefore.toString())) {
            sleep(mobileContext, "before.execution.delay");
        }
    }

    @Override
    public void afterExecution(MobileContext mobileContext) {
        Object sleepBefore = mobileContext.getOption("sleep.before.execution");
        if (sleepBefore != null && Boolean.parseBoolean(sleepBefore.toString())) {
            sleep(mobileContext, "after.execution.delay");
        }
        Object screenshotEnabled = mobileContext.getOption("screenshot.after.execution");
        if (screenshotEnabled != null && Boolean.parseBoolean(screenshotEnabled.toString())) {
            screenshot(mobileContext, "final.png");
        }
    }

    @Override
    public void beforeKeyword(MobileContext mobileContext, String keyword) {
        Object sleepBefore = mobileContext.getOption("sleep.before.keyword");
        if (sleepBefore != null && Integer.parseInt(sleepBefore.toString()) > 0) {
            sleep(mobileContext, "sleep.before.keyword");
        }
    }

    @Override
    public void afterKeyword(MobileContext mobileContext, String keyword) {
        Object screenshotEnabled = mobileContext.getOption("screenshot.after.keyword");
        if (screenshotEnabled != null && Boolean.parseBoolean(screenshotEnabled.toString())) {
            screenshot(mobileContext, keyword + ".png");
        }
        Object sleepBefore = mobileContext.getOption("sleep.after.keyword");
        if (sleepBefore != null && Integer.parseInt(sleepBefore.toString()) > 0) {
            sleep(mobileContext, "sleep.after.keyword");
        }
    }

    private void sleep(MobileContext mobileContext, String delayKey) {
        Object delayObj = mobileContext.getOption(delayKey);
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

    private void screenshot(MobileContext mobileContext, String fileName) {
//        fileName = "screenshot_" + LocalDateTime.now() + ".png";
        mobileContext.getAutomation().screenshot(fileName);
    }
}