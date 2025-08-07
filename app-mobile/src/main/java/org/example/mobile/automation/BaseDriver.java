package org.example.mobile.automation;


public interface BaseDriver {
    void launchApp(String appId);
    void closeApp(String appId);
    Element findElement(By by);
    void click(By by);
    void sendKeys(By by, String text);
    String getText(By by);
    void swipe(int startX, int startY, int endX, int endY, int durationMs);
    void back();
    void screenshot(String savePath);
}