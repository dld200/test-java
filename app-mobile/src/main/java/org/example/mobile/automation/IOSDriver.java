//package org.example.mobile.automation;
//
//
//public class IOSDriver implements BaseDriver {
//
//    @Override
//    public void launchApp(String appId) {
//        // 物理设备：使用 libimobiledevice 或 xcrun xcuitest
//        XCUITestUtils.runCommand("idevicedebug run " + appId);
//    }
//
//    @Override
//    public void closeApp(String appId) {
//        XCUITestUtils.runCommand("idevicediagnostics restart"); // 或直接 kill
//    }
//
//    @Override
//    public Element findElement(By by) {
//        return XCUITestUtils.findElement(by);
//    }
//
//    @Override
//    public void click(By by) {
//        Element e = findElement(by);
//        XCUITestUtils.tap(e.getCenterX(), e.getCenterY());
//    }
//
//    @Override
//    public void sendKeys(By by, String text) {
//        Element e = findElement(by);
//        XCUITestUtils.typeText(e, text);
//    }
//
//    @Override
//    public String getText(By by) {
//        return findElement(by).getText();
//    }
//
//    @Override
//    public void swipe(int startX, int startY, int endX, int endY, int durationMs) {
//        XCUITestUtils.swipe(startX, startY, endX, endY, durationMs);
//    }
//
//    @Override
//    public void back() {
//        XCUITestUtils.tapBack();
//    }
//
//    @Override
//    public void screenshot(String savePath) {
//        XCUITestUtils.runCommand("idevicescreenshot " + savePath);
//    }
//}
