package org.example.mobile.automation.android;

import lombok.extern.slf4j.Slf4j;
import org.example.mobile.automation.Automation;
import org.example.mobile.automation.UIElementParser;
import org.example.mobile.automation.UiElement;
import org.example.mobile.dto.Device;
import org.example.mobile.util.ShellUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Android自动化实现类
 */
@Slf4j
public class AndroidAutomation implements Automation {
    private String deviceId;
    private final UIElementParser parser = new AndroidSourceParser();
    private UiElement currentTree;

    private static final Pattern KEY_VALUE = Pattern.compile("(\\S+):(\\S+)");

    @Override
    public List<Device> listDevices() {
        String[] lines = ShellUtil.exec("adb devices -l").split("\\n");
        List<Device> devices = new ArrayList<>();
        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty() || line.startsWith("List of devices")) continue;

            String[] parts = line.split("\\s+");
            if (parts.length < 2) continue;

            String udid = parts[0];
            String status = parts[1].equals("device")?"online":"offline";

            Device device = new Device();
            device.setUdid(udid);
            device.setStatus(status);
            device.setPlatform("android");  // adb 设备默认android
            device.setSimulator(false);     // adb连线设备默认不是模拟器

            // 解析 key:value 字段
            for (int i = 2; i < parts.length; i++) {
                Matcher m = KEY_VALUE.matcher(parts[i]);
                if (m.matches()) {
                    String key = m.group(1);
                    String value = m.group(2);
                    switch (key) {
                        case "model":
                        case "product":
                            device.setName(value);
                            break;
                        // 这里可以根据需求扩展其它字段
                    }
                }
            }
            devices.add(device);
        }
        deviceIds.addAll(devices.stream().map(Device::getUdid).toList());
        return devices;
    }

    @Override
    public boolean connect(String deviceId) {
        this.deviceId = deviceId;
        return ShellUtil.exec(String.format("adb -s %s get-state", deviceId)).equals("device");
    }

    @Override
    public boolean launch(String bundleId) {
        ShellUtil.exec(String.format("adb -s %s shell monkey -p %s -c android.intent.category.LAUNCHER 1", deviceId, bundleId));
        return true;
    }

    @Override
    public String source() {
        String dump = ShellUtil.exec(String.format("adb -s %s exec-out uiautomator dump /dev/tty", deviceId));
        int start = dump.indexOf("<?xml");
        int end = dump.indexOf("</hierarchy>");
        if (start >= 0 && end > start) {
            dump = dump.substring(start, end + "</hierarchy>".length());
        }
        return dump;
    }

    public UiElement findElement(String elementId) {
        if (currentTree != null) {
            Optional<UiElement> e = currentTree.findByName(elementId).stream().findFirst();
            if (e.isPresent()) {
                return e.get();
            }
        }
        UiElement element = parser.parse(source());
        currentTree = element;
        return element.findByName(elementId).stream().findFirst().orElse(null);
    }

    @Override
    public String click(String elementId) {
        UiElement e = findElement(elementId);
        int cx = e.x + e.width / 2;
        int cy = e.y + e.height / 2;
        return ShellUtil.exec(String.format("adb -s %s shell input tap %s %s", deviceId, cx, cy));
    }

    @Override
    public String input(String elementId, String text) {
        click(elementId);
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
        }
        text = text.replace(" ", "%s")
                .replace("'", "\\'")
                .replace("\"", "\\\"");
        ShellUtil.exec(String.format("adb -s %s shell input text %s", deviceId, text));
        return ShellUtil.exec(String.format("adb -s %s shell input keyevent 66", deviceId));
    }

    @Override
    public String screenshot(String localFileName) {
        ShellUtil.exec(String.format("adb -s %s shell screencap -p %s", deviceId, "/sdcard/xx.png"));
        ShellUtil.exec(String.format("adb -s %s pull %s %s", deviceId, "/sdcard/xx.png", localFileName));
        return localFileName;
    }

    @Override
    public boolean swipe(String direction) {
        switch (direction) {
            case "left":
                ShellUtil.exec(String.format("adb -s %s shell input swipe 5 1200 300 1200 500", deviceId));
                break;
            case "right":
                ShellUtil.exec(String.format("adb -s %s shell input swipe 200 1600 900 1200 500", deviceId));
                break;
            case "up":
                ShellUtil.exec(String.format("adb -s %s shell input swipe 540 1600 540 800 500", deviceId));
                break;
            case "down":
                ShellUtil.exec(String.format("adb -s %s shell input swipe 540 800 540 1600 500", deviceId));
                break;
            default:
                break;
        }
        return true;
    }

    public String terminate(String bundleId) {
        return ShellUtil.exec(String.format("adb -s %s shell am force-stop %s", deviceId, bundleId));
    }

    public static void main(String[] args) throws Exception {
        String deviceId = "53F5T19905000341";
        Automation robot = new AndroidAutomation();

        System.out.println(robot.listDevices());

        robot.connect(deviceId);

        // 获取元素并点击
//        String ok = robot.click("search_input");
//        System.out.println("click result: " + ok);

        // 输入文本示例
        robot.input("search_input", "hello world");

        // 滑动示例
        robot.swipe("left");

        // 截图示例
        String f = robot.screenshot("screen.png");
        System.out.println("saved screenshot: " + f);
    }
}
