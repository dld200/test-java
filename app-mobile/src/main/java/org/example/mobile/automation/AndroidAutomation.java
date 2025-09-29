package org.example.mobile.automation;

import lombok.extern.slf4j.Slf4j;
import org.example.mobile.automation.source.UiElement;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Android自动化实现类
 */
@Slf4j
public class AndroidAutomation implements Automation {


    private final String deviceId;

    public AndroidAutomation(String deviceId) {
        this.deviceId = deviceId;
    }

    @Override
    public boolean launch(String deviceId, String bundleId) {
        execute("shell", "monkey", "-p", bundleId, "-c", "android.intent.category.LAUNCHER", "1");
        return true;
    }

    @Override
    public String source() {
        String dump = execute("exec-out", "uiautomator", "dump", "/dev/tty");
        int start = dump.indexOf("<?xml");
        int end = dump.indexOf("</hierarchy>");
        if (start >= 0 && end > start) {
            dump = dump.substring(start, end + "</hierarchy>".length());
        }
        return dump;
    }

    //todo:
    public UiElement findElement(String elementId) {
        return null;
    }

    @Override
    public String click(String elementId) {
        UiElement e = findElement(elementId);
        int cx = e.x + e.width / 2;
        int cy = e.y + e.height / 2;
        return execute("shell", "input", "tap", String.valueOf(cx), String.valueOf(cy));
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
        return execute("shell", "input", "text", text);
    }

    @Override
    public String screenshot(String localFileName) {
        String remote = "/sdcard/" + localFileName;
        execute("shell", "screencap", "-p", remote);
        execute("pull", remote, localFileName);
        return localFileName;
    }

    @Override
    public boolean swipe(String direction) {
        switch (direction) {
            case "left":
                execute("shell", "input", "swipe", "0", "200", "400", "200", "500");
                break;
            case "right":
                execute("shell", "input", "swipe", "400", "200", "0", "200", "500");
                break;
            case "up":
                execute("shell", "input", "swipe", "50", "500", "50", "20", "500");
                break;
            case "down":
                break;
            default:
                break;
        }
        return true;
    }

    private String execute(String... args) {
        List<String> cmd = new ArrayList<>();
        cmd.add("adb");
        cmd.add("-s");
        cmd.add(deviceId);
        cmd.addAll(Arrays.asList(args));
        StringBuilder sb = new StringBuilder();
        ProcessBuilder pb = new ProcessBuilder(cmd);
        pb.redirectErrorStream(true);
        try {
            Process process = pb.start();
            boolean finished = process.waitFor(30, TimeUnit.SECONDS);
            if (!finished) {
                process.destroyForcibly();
            }
            InputStream is = process.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
        } catch (Exception e) {

        }
        return sb.toString();
    }

    //    @Override
    public void terminate(String bundleId) throws IOException, InterruptedException {
        execute("shell", "am", "force-stop", bundleId);
    }

    public static void main(String[] args) throws Exception {
        String deviceId = "53F5T19905000341";
        Automation robot = new AndroidAutomation(deviceId);

        // 获取元素并点击
        String ok = robot.click("ca.snappay.snaplii.test:id/button1");
        System.out.println("click result: " + ok);

        // 输入文本示例
        robot.input("ca.snappay.snaplii.test:id/inputField", "hello world");

        // 滑动示例
        robot.swipe("up");

        // 截图示例
        String f = robot.screenshot("screen.png");
        System.out.println("saved screenshot: " + f);
    }
}
