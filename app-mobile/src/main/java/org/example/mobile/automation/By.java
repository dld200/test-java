package org.example.mobile.automation;

public class By {
    public enum Strategy {
        ID, XPATH, TEXT, CLASS
    }

    private Strategy strategy;
    private String value;

    private By(Strategy strategy, String value) {
        this.strategy = strategy;
        this.value = value;
    }

    public static By id(String id) {
        return new By(Strategy.ID, id);
    }

    public static By xpath(String xpath) {
        return new By(Strategy.XPATH, xpath);
    }

    public static By text(String text) {
        return new By(Strategy.TEXT, text);
    }

    public static By clazz(String clazz) {
        return new By(Strategy.CLASS, clazz);
    }

    public Strategy getStrategy() {
        return strategy;
    }

    public String getValue() {
        return value;
    }
}
