package org.example.mobile.automation;

public class Element {
    private int x;          // 元素左上角 X 坐标
    private int y;          // 元素左上角 Y 坐标
    private int width;      // 元素宽度
    private int height;     // 元素高度
    private String text;    // 元素文本
    private String resourceId;  // 元素 ID（可选）
    private String className;   // 元素类型（可选）

    public Element(int x, int y, int width, int height, String text) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.text = text;
    }

    public Element(int x, int y, int width, int height, String text, String resourceId, String className) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.text = text;
        this.resourceId = resourceId;
        this.className = className;
    }

    // 中心点坐标
    public int getCenterX() {
        return x + width / 2;
    }

    public int getCenterY() {
        return y + height / 2;
    }

    // Getter
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public String getText() {
        return text;
    }

    public String getResourceId() {
        return resourceId;
    }

    public String getClassName() {
        return className;
    }

    // 便于调试
    @Override
    public String toString() {
        return "Element{" +
                "x=" + x +
                ", y=" + y +
                ", width=" + width +
                ", height=" + height +
                ", text='" + text + '\'' +
                ", resourceId='" + resourceId + '\'' +
                ", className='" + className + '\'' +
                '}';
    }
}
