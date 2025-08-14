package org.example.mobile.automation;

public class WDATest {
    public static void main(String[] args) {
        String deviceId = "F0F99D79-FCB0-45C3-AD55-89CCCA9BDBFD";
        String bundleId = "ca.snappay.snaplii.test";
//        String sessionId = "CD2348B2-F623-4E42-86D3-C6E10CC68AA0";

        // 启动 WDA
        WDAUtils.launchWDA(deviceId, bundleId);

        // 输入文本
        String userId = WDAUtils.findElement("id", "Search from over 200 top brands");
        WDAUtils.clickElement(userId);

        String search = WDAUtils.findElement("id", "Enter a brand name");
        WDAUtils.sendKeys(search, "amazon");


        // 截图
        String fileName = WDAUtils.screenshot();
        System.out.println("Screenshot: " + fileName);

        WDAUtils.swipe("left");

        // 查找元素并点击
        String elementId = WDAUtils.findElement("name", "Log in/Sign up");
        WDAUtils.clickElement(elementId);

    }
}
