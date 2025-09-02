package org.example.mobile.device;

/**
 * 自动化接口
 */
public interface Automation {

//    void init(Map<String, Object> options);

    void setup(String deviceId, String bundleId);

    String source();

    void click(String elementId);

    void input(String elementId, String text);

    Object screenshot(String fileName);

    void swipe(String direction);
}