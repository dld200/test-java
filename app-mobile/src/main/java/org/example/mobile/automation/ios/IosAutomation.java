package org.example.mobile.automation.ios;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.extern.slf4j.Slf4j;
import org.example.mobile.automation.Automation;
import org.example.mobile.automation.UIElementParser;
import org.example.mobile.automation.UiElement;
import org.example.mobile.automation.android.AndroidSourceParser;
import org.example.mobile.dto.Device;
import org.example.mobile.util.HttpUtil;
import org.example.mobile.util.ShellUtil;
import org.springframework.util.StringUtils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Slf4j
public class IosAutomation implements Automation {

    private final String WDA_URL = "http://127.0.0.1:8100";
    private String projectPath = "/Users/snap/workspace/WebDriverAgent";
    private String sessionId;
    private String deviceId;
    private final UIElementParser parser = new AndroidSourceParser();
    private UiElement currentTree;

    @Override
    public boolean connect(String deviceId) {
        this.deviceId = deviceId;
        // 启动 WebDriverAgentRunner
        String platform = deviceId.length() == "00008120-000A79100AE0201".length() ? "iOS" : "iOS Simulator";
        // 启动wda功能
        new Thread(() -> {
            // ShellUtil.exec("xcrun simctl launch " + deviceId + " xx.facebook.WebDriverAgentRunner");
            String cmd = String.format(
                    "xcodebuild -project %s/WebDriverAgent.xcodeproj " +
                            "-scheme WebDriverAgentRunner " +
                            "-destination 'platform=%s,id=%s' test",
                    projectPath, platform, deviceId);
            ShellUtil.exec(cmd);
        }).start();
        // 真机代理
        if(platform.equals("iOS")) {
            new Thread(() -> {
                ShellUtil.exec("iproxy 8100 8100");
            }).start();
        }
        return waitForWDA();
    }

    /**
     * 重建session
     */
    @Override
    public boolean launch(String bundleId) {
        createSession(bundleId);
        return waitForWDA();
    }

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
    public String click(String name) {
        String elementId = findElement("id", name);
        UiElement e = getElementRect(elementId);
        return tap(e.getX() + e.getWidth() / 2, e.getY() + e.getHeight() / 2);
    }

    @Override
    public String input(String name, String text) {
        String elementId = findElement("id", name);
        String body = String.format("{\"value\":[\"%s\"]}", text);
        return HttpUtil.sendPost(WDA_URL + "/session/" + sessionId + "/element/" + elementId + "/value", body);
    }

    @Override
    public String screenshot(String fileName) {
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
    public boolean swipe(String direction) {
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
                swipe(50, 20, 50, 500, 0.1f);
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public List<Device> listDevices() {
        List<Device> devices = new ArrayList<>();
        String usbInfo = ShellUtil.exec("system_profiler SPUSBDataType");
        String lines = ShellUtil.exec("xcrun xctrace list devices");
        boolean simulator = false;
        for (String line : lines.split("\n")) {
            // 解析设备信息
            if (line.startsWith("== Devices")) {
                continue;
            } else if (line.startsWith("== Simulators ==")) {
                break;
            }
            if (line.contains(") (")) {
                String[] parts = line.split("\\) \\(");
                String udid = parts[1].substring(0, parts[1].length() - 1);
                int index = parts[0].lastIndexOf(" (");
                String name = parts[0].substring(0, index);
                String os = parts[0].substring(index + 2);
                Device device = Device.builder()
                        .name(name)
                        .platform("ios")
                        .udid(udid)
                        .status(usbInfo.contains(udid.replace("-", "")) ? "online" : "offline")
                        .simulator(simulator)
                        .os(os)
                        .build();
                devices.add(device);
            }
        }

        String simulators = ShellUtil.exec("xcrun simctl list devices");

        String os = "";
        for (String line : simulators.split("\n")) {
            // 解析设备信息
            if (line.startsWith("== Devices ==")) {
                continue;
            } else if (line.startsWith("--")) {
                os = line.replaceAll("--", "").trim();
                continue;
            }
            if (line.contains("(Booted)")) {
                line = line.replace(") (Booted)", "");
                int index = line.lastIndexOf("(");
                String udid = line.substring(index + 1).trim();
                String name = line.substring(0, index).trim();
                Device device = Device.builder()
                        .name(name)
                        .platform("ios")
                        .udid(udid)
                        .status("online")
                        .simulator(true)
                        .os(os)
                        .build();
                devices.add(device);
            }
        }
        return devices;
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
    private boolean waitForWDA() {
        int retries = 30;
        while (retries-- > 0) {
            if (isWDARunning()) return true;
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignored) {
            }
        }
        return false;
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

    /**
     * 从多元素中找最合适的元素
     */
    private String findElement(String using, String value) {
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
        throw new RuntimeException("Element not found: " + res);
    }

    /**
     * 检查元素属性
     */
    private Object getElementAttribute(String attr, String elementId) {
        String res = HttpUtil.sendGet(WDA_URL + "/session/" + sessionId + "/element/" + elementId + "/attribute/" + attr);
        JsonObject json = JsonParser.parseString(res).getAsJsonObject();
        return json.get("value");
    }

    /**
     * 获取元素坐标
     */
    private UiElement getElementRect(String elementId) {
        String res = HttpUtil.sendGet(WDA_URL + "/session/" + sessionId + "/element/" + elementId + "/rect");
        JsonObject json = JsonParser.parseString(res).getAsJsonObject();
        return new UiElement(json.getAsJsonObject("value").get("x").getAsInt(),
                json.getAsJsonObject("value").get("y").getAsInt(),
                json.getAsJsonObject("value").get("width").getAsInt(),
                json.getAsJsonObject("value").get("height").getAsInt());
    }

    private void pressButton(String buttonName) {
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
        String res = HttpUtil.sendGet(WDA_URL + "/session/" + sessionId + "/element/" + elementId + "/text");
        JsonObject json = JsonParser.parseString(res).getAsJsonObject();
        return json.get("value").getAsString();
    }

    /**
     * 点击坐标
     */
    private String tap(int x, int y) {
        String body = String.format("{\"x\":%d,\"y\":%d}", x, y);
        return HttpUtil.sendPost(WDA_URL + "/session/" + sessionId + "/wda/tap", body);
    }

    /**
     * 滑动
     */
    private void swipe(int startX, int startY, int endX, int endY, float seconds) {
        String body = String.format("{\"fromX\":%d,\"fromY\":%d,\"toX\":%d,\"toY\":%d,\"duration\":%s}",
                startX, startY, endX, endY, seconds);
        HttpUtil.sendPost(WDA_URL + "/session/" + sessionId + "/wda/dragfromtoforduration", body);
    }
}