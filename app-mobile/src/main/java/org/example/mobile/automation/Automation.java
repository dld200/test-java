package org.example.mobile.automation;

import org.example.mobile.dto.Device;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public interface Automation {
    Set<String> deviceIds = new HashSet<>();

    List<Device> listDevices();

    /**
     * 指定设备
     */
    boolean connect(String deviceId);

    /**
     * ios必须调用重建session，android可以不调用
     */
    boolean launch(String bundleId);

    String source();

    String click(String elementId);

    String input(String elementId, String text);

    String screenshot(String fileName);

    boolean swipe(String direction);
}