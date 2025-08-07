package org.example.mobile.automation;

import java.io.FileOutputStream;
import java.util.Base64;

public class SimctlUtils {

    public static String runCommand(String cmd) {
        return XCUITestUtils.runCommand(cmd);
    }

    /** Boot 模拟器 */
    public static void bootSimulator(String deviceId) {
        String devices = runCommand("xcrun simctl list devices");
        if (!devices.contains(deviceId + " (Booted)")) {
            runCommand("xcrun simctl boot " + deviceId);
            XCUITestUtils.runCommand("open -a Simulator");
        }
    }

    /** 检查 App 是否已安装 */
    public static boolean isAppInstalled(String deviceId, String bundleId) {
        String output = runCommand("xcrun simctl listapps " + deviceId);
        return output.contains(bundleId);
    }

    /** 保存 Base64 截图到文件 */
    public static void saveBase64ToFile(String base64, String path) {
        try (FileOutputStream fos = new FileOutputStream(path)) {
            byte[] data = Base64.getDecoder().decode(base64);
            fos.write(data);
        } catch (Exception e) {
            throw new RuntimeException("Save screenshot failed: " + e.getMessage(), e);
        }
    }
}
