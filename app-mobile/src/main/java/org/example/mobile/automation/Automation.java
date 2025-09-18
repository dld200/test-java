package org.example.mobile.automation;

import org.example.common.domain.TestDevice;

import java.util.List;

public interface Automation {

    public List<TestDevice> listDevices();

//    void init(Map<String, Object> options);

    boolean setup(String deviceId, String bundleId);

    String source();

    boolean click(String elementId);

    boolean input(String elementId, String text);

    Object screenshot(String fileName);

    boolean swipe(String direction);
}