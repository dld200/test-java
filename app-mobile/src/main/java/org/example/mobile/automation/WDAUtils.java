package org.example.mobile.automation;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Base64;

public class WDAUtils {
    private static String WDA_URL = "http://127.0.0.1:8100";
    private static String sessionId;
    private static String projectPath;

    /**
     * 启动 WDA, 同时拉起被测应用
     */
    public static void launchWDA(String deviceId, String bundleId) {
        // 启动 WebDriverAgentRunner
        String cmd = String.format(
                "xcodebuild -project %s/WebDriverAgent.xcodeproj " +
                        "-scheme WebDriverAgentRunner " +
                        "-destination 'platform=iOS Simulator,id=%s' test",
                projectPath, deviceId);

        // 异步执行
        new Thread(() -> {
            try {
                ProcessBuilder pb = new ProcessBuilder("/bin/sh", "-c", cmd);
                pb.redirectErrorStream(true);
                pb.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        //check session
        if (isWDARunning() && sessionId != null) {
            return;
        } else {
            XCUITestUtils.runCommand("xcrun simctl launch " + deviceId + " xx.facebook.WebDriverAgentRunner");
            createSession(bundleId);
        }
        waitForWDA();
    }

    public static void launchWDA(String deviceId, String bundleId, String sessionId) {
        WDAUtils.sessionId = sessionId;
    }


    /**
     * 检查 WDA 是否可用
     */
    public static boolean isWDARunning() {
        try {
            String res = HttpUtils.sendGet(WDA_URL + "/status");
            JsonObject json = JsonParser.parseString(res).getAsJsonObject();
            sessionId = json.get("sessionId").getAsString();
            return res.contains("success");
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 等待 WDA 启动
     */
    private static void waitForWDA() {
        int retries = 30;
        while (retries-- > 0) {
            if (isWDARunning()) return;
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignored) {
            }
        }
        throw new RuntimeException("WDA did not start in time");
    }

    /**
     * 创建 session
     */
    public static void createSession(String bundleId) {
        String body = String.format("{\"capabilities\":{\"alwaysMatch\":{\"platformName\":\"iOS\",\"bundleId\":\"%s\"}}}", bundleId);
        String res = HttpUtils.sendPost(WDA_URL + "/session", body);

        JsonObject json = JsonParser.parseString(res).getAsJsonObject();
        JsonObject value = json.getAsJsonObject("value");
        sessionId = value.has("sessionId") ? value.get("sessionId").getAsString() : value.getAsJsonObject().get("sessionId").getAsString();

        if (sessionId == null || sessionId.isEmpty()) {
            throw new RuntimeException("Failed to create WDA session: " + res);
        }
    }

    /**
     * 从多元素中找最合适的元素
     */
    public static String findElement(String using, String value) {
        ensureSession();
        String body = String.format("{\"using\":\"%s\",\"value\":\"%s\"}", using, value);
        String res = HttpUtils.sendPost(WDA_URL + "/session/" + sessionId + "/elements", body);

        JsonObject json = JsonParser.parseString(res).getAsJsonObject();
        if (json.has("value")) {
            for (JsonElement element : json.getAsJsonArray("value")) {
                String id = element.getAsJsonObject().get("ELEMENT").getAsString();
                if (getElementAttribute("visible", id).toString().equals("true")) {
                    return id;
                }
            }
        }
        throw new RuntimeException("Element not found: " + res);
    }


    /**
     * 获取页面元素树
     */
    public static String getPageSource() {
        String res = HttpUtils.sendGet(WDA_URL + "/session/" + sessionId + "/source");
        JsonObject json = JsonParser.parseString(res).getAsJsonObject();
        if (json.has("value")) {
            return json.get("value").getAsString();
        }
        throw new RuntimeException("Element not found: " + res);
    }


    /**
     * 检查元素属性
     */
    public static Object getElementAttribute(String attr, String elementId) {
        ensureSession();
        String res = HttpUtils.sendGet(WDA_URL + "/session/" + sessionId + "/element/" + elementId + "/attribute/" + attr);
        JsonObject json = JsonParser.parseString(res).getAsJsonObject();
        return json.get("value");
    }

    /**
     * 获取元素坐标
     */
    public static Element getElementRect(String elementId) {
        ensureSession();
        String res = HttpUtils.sendGet(WDA_URL + "/session/" + sessionId + "/element/" + elementId + "/rect");
        JsonObject json = JsonParser.parseString(res).getAsJsonObject();
        return new Element(json.getAsJsonObject("value").get("x").getAsInt(),
                json.getAsJsonObject("value").get("y").getAsInt(),
                json.getAsJsonObject("value").get("width").getAsInt(),
                json.getAsJsonObject("value").get("height").getAsInt(),
                "");
    }

    /**
     * 点击元素
     */
    public static void clickElement(String elementId) {
        ensureSession();
        Element e = getElementRect(elementId);
        WDAUtils.tap(e.getX() + e.getWidth() / 2, e.getY() + e.getHeight() / 2);
//        HttpUtils.sendPost(WDA_URL + "/session/" + sessionId + "/element/" + elementId + "/click", "{}");
    }

    /**
     * 输入文本
     */
    public static void sendKeys(String elementId, String text) {
        ensureSession();
        String body = String.format("{\"value\":[\"%s\"]}", text);
        HttpUtils.sendPost(WDA_URL + "/session/" + sessionId + "/element/" + elementId + "/value", body);
    }

    public static void pressButton(String buttonName) {
        ensureSession();
//        const _map = {
//                "HOME": "home",
//                "VOLUME_UP": "volumeup",
//                "VOLUME_DOWN": "volumedown",
//		};
        HttpUtils.sendPost(WDA_URL + "/session/" + sessionId + "/wda/pressbutton", "{\"name\":\"" + buttonName + "\"}");
    }

    /**
     * 获取文本
     */
    public static String getText(String elementId) {
        ensureSession();
        String res = HttpUtils.sendGet(WDA_URL + "/session/" + sessionId + "/element/" + elementId + "/text");
        JsonObject json = JsonParser.parseString(res).getAsJsonObject();
        return json.get("value").getAsString();
    }

    /**
     * 点击坐标
     */
    public static void tap(int x, int y) {
        ensureSession();
        String body = String.format("{\"x\":%d,\"y\":%d}", x, y);
        HttpUtils.sendPost(WDA_URL + "/session/" + sessionId + "/wda/tap", body);
    }

    /**
     * 滑动
     */
    public static void swipe(int startX, int startY, int endX, int endY, float seconds) {
        ensureSession();
        String body = String.format("{\"fromX\":%d,\"fromY\":%d,\"toX\":%d,\"toY\":%d,\"duration\":%s}",
                startX, startY, endX, endY, seconds);
        HttpUtils.sendPost(WDA_URL + "/session/" + sessionId + "/wda/dragfromtoforduration", body);
    }

    /**
     * 截图
     */
    public static String screenshot() {
        ensureSession();
        String res = HttpUtils.sendGet(WDA_URL + "/session/" + sessionId + "/screenshot");
        JsonObject json = JsonParser.parseString(res).getAsJsonObject();

        //base64 to image
        byte[] imageBytes = Base64.getDecoder().decode(json.get("value").getAsString());
        String fileName = "screenshot_" + LocalDateTime.now() + ".png";
        try {
            FileOutputStream outputStream = new FileOutputStream(fileName);
            outputStream.write(imageBytes);
            outputStream.close();
        } catch (IOException e) {

        }
        return fileName;
    }

    private static void ensureSession() {
        if (sessionId == null || sessionId.isEmpty()) {
            throw new IllegalStateException("WDA session not created. Call createSession() first.");
        }
    }

    public static void swipe(String direction) {
        switch (direction) {
            case "left":
                WDAUtils.swipe(0, 200, 400, 200, 0.01f);
                break;
            case "right":
                WDAUtils.swipe(400, 200, 0, 200, 0.01f);
                break;
            case "up":
                WDAUtils.swipe(50, 500, 50, 20, 0.01f);
                break;
            case "down":
                break;
            default:
                break;
        }
    }

}
