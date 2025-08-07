package org.example.mobile.automation;


public class IOSSimulatorDriver implements BaseDriver {

    private String deviceId;   // 模拟器 UDID
    private String bundleId;   // 当前操作的 App Bundle ID

    public IOSSimulatorDriver(String deviceId, String bundleId) {
        this.deviceId = deviceId;
        this.bundleId = bundleId;

        // 启动模拟器（如果没启动）
        SimctlUtils.bootSimulator(deviceId);

        // 启动 WDA（如果没启动）
        if (!WDAUtils.isWDARunning()) {
            WDAUtils.launchWDA(deviceId, bundleId);
        }
    }

    @Override
    public void launchApp(String appId) {
        this.bundleId = appId;
        // 安装检查（如果未安装需要先安装）
        if (!SimctlUtils.isAppInstalled(deviceId, appId)) {
            throw new RuntimeException("App not installed: " + appId);
        }
        SimctlUtils.runCommand("xcrun simctl launch " + deviceId + " " + appId);
    }

    @Override
    public void closeApp(String appId) {
        SimctlUtils.runCommand("xcrun simctl terminate " + deviceId + " " + appId);
    }

    @Override
    public Element findElement(By by) {
        String using;
        switch (by.getStrategy()) {
            case ID:
                using = "id";
                break;
            case XPATH:
                using = "xpath";
                break;
            case TEXT:
                using = "name";
                break;
            case CLASS:
                using = "class name";
                break;
            default:
                throw new IllegalArgumentException("Unsupported strategy: " + by.getStrategy());
        }
        String elementId = WDAUtils.findElement(using, by.getValue());
        return new Element(0, 0, 0, 0, "", elementId, null);
    }

    @Override
    public void click(By by) {
        Element e = findElement(by);
        WDAUtils.clickElement(e.getResourceId());
    }

    @Override
    public void sendKeys(By by, String text) {
        Element e = findElement(by);
        WDAUtils.sendKeys(e.getResourceId(), text);
    }

    @Override
    public String getText(By by) {
        Element e = findElement(by);
        return WDAUtils.getText(e.getResourceId());
    }

    @Override
    public void swipe(int startX, int startY, int endX, int endY, int durationMs) {
        WDAUtils.swipe(startX, startY, endX, endY, durationMs);
    }

    @Override
    public void back() {
        // iOS 无物理返回，通常用左滑或点击导航栏返回按钮
        // 可实现为 swipe 或查找 "Back" 按钮点击
        WDAUtils.tap(20, 50); // 典型返回按钮位置（可优化）
    }

    @Override
    public void screenshot(String savePath) {
        String base64 = WDAUtils.screenshot();
        SimctlUtils.saveBase64ToFile(base64, savePath);
    }
}
