package org.example.mobile.automation;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.extern.slf4j.Slf4j;
import org.example.common.domain.Device;
import org.example.mobile.util.HttpUtil;
import org.example.mobile.util.WDAUtil;
import org.springframework.util.StringUtils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;

@Slf4j
public class IosSimulatorAutomation implements Automation {

    private final String WDA_URL = "http://127.0.0.1:8100";
    private String projectPath;
    private String sessionId;


    @Override
    public String source() {
        String res = HttpUtil.sendGet(WDA_URL + "/session/" + sessionId + "/source");
        JsonObject json = JsonParser.parseString(res).getAsJsonObject();
        if (json.has("value")) {
            return json.get("value").getAsString();
        }
        throw new RuntimeException("Element not found: " + res);
    }

    @Override
    public void click(String name) {
        String elementId = findElement("id", name);
        Element e = getElementRect(elementId);
        tap(e.getX() + e.getWidth() / 2, e.getY() + e.getHeight() / 2);
    }

    @Override
    public void input(String name, String text) {
        ensureSession();
        String elementId = findElement("id", name);
        String body = String.format("{\"value\":[\"%s\"]}", text);
        HttpUtil.sendPost(WDA_URL + "/session/" + sessionId + "/element/" + elementId + "/value", body);
    }

    @Override
    public Object screenshot(String fileName) {
        ensureSession();
        String res = HttpUtil.sendGet(WDA_URL + "/session/" + sessionId + "/screenshot");
        JsonObject json = JsonParser.parseString(res).getAsJsonObject();
        //base64 to image
        byte[] imageBytes = Base64.getDecoder().decode(json.get("value").getAsString());
        if (StringUtils.isEmpty(fileName)) {
            fileName = "screenshot_" + LocalDateTime.now() + ".png";
        }
        try {
            FileOutputStream outputStream = new FileOutputStream(fileName);
            outputStream.write(imageBytes);
            outputStream.close();
        } catch (IOException e) {

        }
        return fileName;
    }

    @Override
    public void swipe(String direction) {
        switch (direction) {
            case "left":
                swipe(0, 200, 400, 200, 0.1f);
                break;
            case "right":
                swipe(400, 200, 0, 200, 0.1f);
                break;
            case "up":
                swipe(50, 500, 50, 20, 0.1f);
                break;
            case "down":
                break;
            default:
                break;
        }
    }

    @Override
    public List<Device> listDevices() {
        return List.of();
    }

    @Override
    public void setup(String deviceId, String bundleId) {
        //check session
        if (isWDARunning() && sessionId != null) {
            return;
        } else {
            WDAUtil.runCommand("xcrun simctl launch " + deviceId + " xx.facebook.WebDriverAgentRunner");
            createSession(bundleId);
        }
        waitForWDA();
    }

    /**
     * 启动 WDA, 同时拉起被测应用
     */
    private void launchWDA(String deviceId) {
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
                log.error("Error: ", e);
            }
        }).start();
    }

    /**
     * 检查 WDA 是否可用
     */
    private boolean isWDARunning() {
        try {
            String res = HttpUtil.sendGet(WDA_URL + "/status");
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
    private void waitForWDA() {
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

    private void createSession(String bundleId) {
        String body = String.format("{\"capabilities\":{\"alwaysMatch\":{\"platformName\":\"iOS\",\"bundleId\":\"%s\"}}}", bundleId);
        String res = HttpUtil.sendPost(WDA_URL + "/session", body);

        JsonObject json = JsonParser.parseString(res).getAsJsonObject();
        JsonObject value = json.getAsJsonObject("value");
        sessionId = value.get("sessionId") != null ? value.get("sessionId").getAsString() : value.getAsJsonObject().get("sessionId").getAsString();

        if (sessionId == null || sessionId.isEmpty()) {
            throw new RuntimeException("Failed to create WDA session: " + res);
        }
    }

    private void ensureSession() {
        if (sessionId == null || sessionId.isEmpty()) {
            throw new IllegalStateException("WDA session not created. Call createSession() first.");
        }
    }

    /**
     * 从多元素中找最合适的元素
     */
    private String findElement(String using, String value) {
        ensureSession();
        String body = String.format("{\"using\":\"%s\",\"value\":\"%s\"}", using, value);
        String res = HttpUtil.sendPost(WDA_URL + "/session/" + sessionId + "/elements", body);

        JsonObject json = JsonParser.parseString(res).getAsJsonObject();
        if (json.has("value")) {
            for (JsonElement element : json.getAsJsonArray("value")) {
                String id = element.getAsJsonObject().get("ELEMENT").getAsString();
                if (getElementAttribute("visible", id).toString().equals("true")) {
                    return id;
                }
            }
        }
        // todo: 从多元素中找最合适的元素
        throw new RuntimeException("Element not found: " + res);
    }

    /**
     * 检查元素属性
     */
    private Object getElementAttribute(String attr, String elementId) {
        ensureSession();
        String res = HttpUtil.sendGet(WDA_URL + "/session/" + sessionId + "/element/" + elementId + "/attribute/" + attr);
        JsonObject json = JsonParser.parseString(res).getAsJsonObject();
        return json.get("value");
    }

    /**
     * 获取元素坐标
     */
    private Element getElementRect(String elementId) {
        ensureSession();
        String res = HttpUtil.sendGet(WDA_URL + "/session/" + sessionId + "/element/" + elementId + "/rect");
        JsonObject json = JsonParser.parseString(res).getAsJsonObject();
        return new Element(json.getAsJsonObject("value").get("x").getAsInt(),
                json.getAsJsonObject("value").get("y").getAsInt(),
                json.getAsJsonObject("value").get("width").getAsInt(),
                json.getAsJsonObject("value").get("height").getAsInt());
    }

    private void pressButton(String buttonName) {
        ensureSession();
//        const _map = {
//                "HOME": "home",
//                "VOLUME_UP": "volumeup",
//                "VOLUME_DOWN": "volumedown",
//		};
        HttpUtil.sendPost(WDA_URL + "/session/" + sessionId + "/wda/pressbutton", "{\"name\":\"" + buttonName + "\"}");
    }

    /**
     * 获取文本
     */
    private String getText(String elementId) {
        ensureSession();
        String res = HttpUtil.sendGet(WDA_URL + "/session/" + sessionId + "/element/" + elementId + "/text");
        JsonObject json = JsonParser.parseString(res).getAsJsonObject();
        return json.get("value").getAsString();
    }

    /**
     * 点击坐标
     */
    private void tap(int x, int y) {
        ensureSession();
        String body = String.format("{\"x\":%d,\"y\":%d}", x, y);
        HttpUtil.sendPost(WDA_URL + "/session/" + sessionId + "/wda/tap", body);
    }

    /**
     * 滑动
     */
    private void swipe(int startX, int startY, int endX, int endY, float seconds) {
        ensureSession();
        String body = String.format("{\"fromX\":%d,\"fromY\":%d,\"toX\":%d,\"toY\":%d,\"duration\":%s}",
                startX, startY, endX, endY, seconds);
        HttpUtil.sendPost(WDA_URL + "/session/" + sessionId + "/wda/dragfromtoforduration", body);
    }
}