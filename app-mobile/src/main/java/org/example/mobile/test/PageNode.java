package org.example.mobile.test;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

class PageNode {
    String id;
    String name; // 页面名称
    String type; // stack/tab/modal
    Map<String, Object> params;
    List<Element> elements; // 页面包含的元素列表

    PageNode(String id, String type) {
        this.id = id;
        this.type = type;
        this.params = new HashMap<>();
        this.elements = new ArrayList<>();
    }
    
    PageNode(String id, String name, String type) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.params = new HashMap<>();
        this.elements = new ArrayList<>();
    }
    
    /**
     * 从JSON字符串解析生成PageNode对象
     * @param json JSON字符串
     */
    PageNode(String json) {
        this.params = new HashMap<>();
        this.elements = new ArrayList<>();
        
        // 简单解析JSON字符串
        parseJson(json);
    }
    
    /**
     * 解析JSON字符串并填充PageNode属性
     * @param json JSON字符串
     */
    private void parseJson(String json) {
        // 移除首尾的大括号
        String content = json.trim();
        if (content.startsWith("{") && content.endsWith("}")) {
            content = content.substring(1, content.length() - 1);
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
                    case "name":
                        this.name = value;
                        break;
                    case "type":
                        this.type = value;
                        break;
                    // 其他字段可以按需处理
                }
            }
        }
    }
    
    /**
     * 添加元素到页面
     * @param element 要添加的元素
     */
    public void addElement(Element element) {
        if (elements == null) {
            elements = new ArrayList<>();
        }
        elements.add(element);
    }
    
    /**
     * 获取页面的所有元素
     * @return 元素列表
     */
    public List<Element> getElements() {
        return elements;
    }
    
    /**
     * 设置页面的元素列表
     * @param elements 元素列表
     */
    public void setElements(List<Element> elements) {
        this.elements = elements;
    }
    
    /**
     * 获取页面ID
     * @return 页面ID
     */
    public String getId() {
        return id;
    }
    
    /**
     * 设置页面ID
     * @param id 页面ID
     */
    public void setId(String id) {
        this.id = id;
    }
    
    /**
     * 获取页面名称
     * @return 页面名称
     */
    public String getName() {
        return name;
    }
    
    /**
     * 设置页面名称
     * @param name 页面名称
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * 获取页面类型
     * @return 页面类型
     */
    public String getType() {
        return type;
    }
    
    /**
     * 设置页面类型
     * @param type 页面类型
     */
    public void setType(String type) {
        this.type = type;
    }
}