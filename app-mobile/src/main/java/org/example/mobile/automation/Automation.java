package org.example.mobile.automation;

public interface Automation {

//    public List<TestDevice> listDevices();

//    void init(Map<String, Object> options);

    boolean launch(String deviceId, String bundleId);

    String source();

    String click(String elementId);

    String input(String elementId, String text);

    String screenshot(String fileName);

    boolean swipe(String direction);
}