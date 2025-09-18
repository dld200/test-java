package org.example.mobile.graph;

/**
 * 页面元素类，表示页面上的一个组件
 */
public class Element {
    private String id;       // 元素ID
    private String type;     // 元素类型
    private String text;     // 元素文本
    private String label;    // 元素标签
    private int x;           // 元素左上角x坐标
    private int y;           // 元素左上角y坐标
    private int width;       // 元素宽度
    private int height;      // 元素高度
    private String targetPageId; // 元素指向的目标页面ID
    
    // 默认构造函数
    public Element() {
    }
    
    // 从JSON字符串解析构造函数
    public Element(String json) {
        parseJson(json);
    }
    
    /**
     * 解析JSON字符串并填充Element属性
     * @param json JSON字符串
     */
    private void parseJson(String json) {
        // 移除首尾的大括号
        String content = json.trim();
        if (content.startsWith("{") && content.endsWith("}")) {
            content = content.substring(1, content.length() - 1).trim();
        }
        
        // 分割键值对
        String[] pairs = content.split(",");
        for (String pair : pairs) {
            String[] keyValue = pair.split(":");
            if (keyValue.length == 2) {
                String key = keyValue[0].trim().replaceAll("\"", "");
                String value = keyValue[1].trim().replaceAll("\"", "");
                
                switch (key) {
                    case "id":
                        this.id = value;
                        break;
                    case "type":
                        this.type = value;
                        break;
                    case "text":
                        this.text = value;
                        break;
                    case "label":
                        this.label = value;
                        break;
                    case "x":
                        this.x = Integer.parseInt(value);
                        break;
                    case "y":
                        this.y = Integer.parseInt(value);
                        break;
                    case "width":
                        this.width = Integer.parseInt(value);
                        break;
                    case "height":
                        this.height = Integer.parseInt(value);
                        break;
                    case "targetPageId":
                        this.targetPageId = value;
                        break;
                }
            }
        }
    }
    
    // 带参数的构造函数（不包含ID）
    public Element(String type, String text, String label, int x, int y, int width, int height) {
        this.type = type;
        this.text = text;
        this.label = label;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
    
    // 带参数的构造函数（包含ID）
    public Element(String id, String type, String text, String label, int x, int y, int width, int height) {
        this.id = id;
        this.type = type;
        this.text = text;
        this.label = label;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
    
    // 带参数的构造函数（包含ID和目标页面ID）
    public Element(String id, String type, String text, String label, int x, int y, int width, int height, String targetPageId) {
        this.id = id;
        this.type = type;
        this.text = text;
        this.label = label;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.targetPageId = targetPageId;
    }
    
    // Getter和Setter方法
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public String getText() {
        return text;
    }
    
    public void setText(String text) {
        this.text = text;
    }
    
    public String getLabel() {
        return label;
    }
    
    public void setLabel(String label) {
        this.label = label;
    }
    
    public int getX() {
        return x;
    }
    
    public void setX(int x) {
        this.x = x;
    }
    
    public int getY() {
        return y;
    }
    
    public void setY(int y) {
        this.y = y;
    }
    
    public int getWidth() {
        return width;
    }
    
    public void setWidth(int width) {
        this.width = width;
    }
    
    public int getHeight() {
        return height;
    }
    
    public void setHeight(int height) {
        this.height = height;
    }
    
    public String getTargetPageId() {
        return targetPageId;
    }
    
    public void setTargetPageId(String targetPageId) {
        this.targetPageId = targetPageId;
    }
    
    @Override
    public String toString() {
        return "Element{" +
                "id='" + id + '\'' +
                ", type='" + type + '\'' +
                ", text='" + text + '\'' +
                ", label='" + label + '\'' +
                ", x=" + x +
                ", y=" + y +
                ", width=" + width +
                ", height=" + height +
                ", targetPageId='" + targetPageId + '\'' +
                '}';
    }
}