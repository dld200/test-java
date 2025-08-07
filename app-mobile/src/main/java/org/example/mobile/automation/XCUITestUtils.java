package org.example.mobile.automation;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class XCUITestUtils {

    public static String runCommand(String cmd) {
        StringBuilder sb = new StringBuilder();
        try {
            Process process = Runtime.getRuntime().exec(new String[]{"/bin/sh", "-c", cmd});
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            process.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    public static Element findElement(By by) {
        // TODO: 集成 XCTest dump，解析元素树
        return new Element(100, 200, 10, 10, "MockElement");
    }

    public static void tap(int x, int y) {
        // TODO: 调用 XCTest UI API 或 sendEvent
    }

    public static void typeText(Element e, String text) {
        // TODO: 聚焦输入框并输入
    }

    public static void swipe(int sx, int sy, int ex, int ey, int durationMs) {
        // TODO: 调用 XCTest swipe API
    }

    public static void tapBack() {
        // TODO: 模拟返回按钮
    }
}
