package org.example.mobile.automation;

import org.example.common.domain.Device;

import java.util.List;

public interface Automation {

    public List<Device> listDevices();

//    void init(Map<String, Object> options);

    void setup(String deviceId, String bundleId);

    String source();

    void click(String elementId);

    void input(String elementId, String text);

    Object screenshot(String fileName);

    void swipe(String direction);
}