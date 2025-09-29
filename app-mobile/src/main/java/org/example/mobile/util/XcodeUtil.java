package org.example.mobile.util;

import lombok.extern.slf4j.Slf4j;
import org.example.mobile.dto.Device;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class XcodeUtil {
    public static List<String> execute(String cmd) {
        List<String> output = new ArrayList<>();
        try {
            Process process = new ProcessBuilder("bash", "-c", cmd).redirectErrorStream(true).start();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()))) {

                String line;
                while ((line = reader.readLine()) != null) {
                    output.add(line);
                }
            }
            process.waitFor();
        } catch (Exception e) {
            log.error("Error running command: ", e);
        }
        return output;
    }

    public static List<Device> listDevices() {
        List<Device> devices = new ArrayList<>();

        List<String> usbInfo = execute("system_profiler SPUSBDataType");
        List<String> lines = execute("xcrun xctrace list devices");
        String status = "";
        boolean simulator = false;
        for (String line : lines) {
            // 解析设备信息
            if (line.startsWith("== Devices")) {
                continue;
            } else if (line.startsWith("== Simulators ==")) {
                break;
            }
            if (line.contains(") (")) {
                String[] parts = line.split("\\) \\(");
                String udid = parts[1].substring(0, parts[1].length() - 2);
                int index = parts[0].lastIndexOf(" (");
                String name = parts[0].substring(0, index);
                String os = parts[0].substring(index + 2);
                Device device = Device.builder()
                        .name(name)
                        .platform("ios")
                        .udid(udid)
                        .status(usbInfo.contains(udid.replace("-", ""))?"online": "offline")
                        .simulator(simulator)
                        .os(os)
                        .build();
                devices.add(device);
            }
        }
        return devices;
    }

    public static List<Device> listSimulators() {
        List<Device> devices = new ArrayList<>();
        List<String> lines = execute("xcrun simctl list devices");

        String os = "";
        for (String line : lines) {
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

    public static void main(String[] args) {
        System.out.println(XcodeUtil.listDevices());
        System.out.println(XcodeUtil.listSimulators());
    }
}
