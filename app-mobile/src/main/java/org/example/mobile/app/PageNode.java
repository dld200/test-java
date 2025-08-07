package org.example.mobile.app;

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
        
        // 解析JSON字符串
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
            content = content.substring(1, content.length() - 1).trim();
        }
        
        // 简单的JSON解析，按逗号分割键值对（不考虑嵌套情况）
        int i = 0;
        while (i < content.length()) {
            // 查找键
            int keyStart = content.indexOf('\"', i);
            if (keyStart == -1) break;
            int keyEnd = content.indexOf('\"', keyStart + 1);
            if (keyEnd == -1) break;
            String key = content.substring(keyStart + 1, keyEnd);
            
            // 查找值的开始位置
            int valueStart = content.indexOf(':', keyEnd);
            if (valueStart == -1) break;
            valueStart++;
            
            // 查找值
            String value = "";
            if (content.charAt(valueStart) == '\"') {
                // 字符串值
                int valueEnd = content.indexOf('\"', valueStart + 1);
                if (valueEnd == -1) break;
                value = content.substring(valueStart + 1, valueEnd);
                i = valueEnd + 1;
            } else if (content.charAt(valueStart) == '[') {
                // 数组值
                int valueEnd = findMatchingBracket(content, valueStart, '[', ']');
                if (valueEnd == -1) break;
                String arrayContent = content.substring(valueStart, valueEnd + 1);
                if ("elements".equals(key)) {
                    parseElements(arrayContent);
                }
                i = valueEnd + 1;
            } else {
                // 其他值（数字、布尔值等）
                int valueEnd = content.indexOf(',', valueStart);
                if (valueEnd == -1) {
                    valueEnd = content.length();
                }
                value = content.substring(valueStart, valueEnd).trim();
                i = valueEnd + 1;
            }
            
            // 根据键设置属性
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
            }
        }
    }
    
    /**
     * 查找匹配的括号
     * @param content 字符串内容
     * @param start 起始位置
     * @param open 开括号
     * @param close 闭括号
     * @return 匹配的括号位置，未找到返回-1
     */
    private int findMatchingBracket(String content, int start, char open, char close) {
        int count = 1;
        for (int i = start + 1; i < content.length(); i++) {
            if (content.charAt(i) == open) {
                count++;
            } else if (content.charAt(i) == close) {
                count--;
                if (count == 0) {
                    return i;
                }
            }
        }
        return -1;
    }
    
    /**
     * 解析elements数组
     * @param arrayContent 数组内容
     */
    private void parseElements(String arrayContent) {
        if (elements == null) {
            elements = new ArrayList<>();
        }
        
        // 移除首尾的方括号
        String content = arrayContent.trim();
        if (content.startsWith("[") && content.endsWith("]")) {
            content = content.substring(1, content.length() - 1).trim();
        }
        
        // 分割数组元素
        List<String> elementJsons = splitJsonArray(content);
        for (String elementJson : elementJsons) {
            if (!elementJson.isEmpty()) {
                elements.add(new Element("{" + elementJson + "}"));
            }
        }
    }
    
    /**
     * 分割JSON数组
     * @param content 数组内容
     * @return 分割后的元素列表
     */
    private List<String> splitJsonArray(String content) {
        List<String> result = new ArrayList<>();
        int start = 0;
        int bracketCount = 0;
        
        for (int i = 0; i < content.length(); i++) {
            char c = content.charAt(i);
            if (c == '{') {
                bracketCount++;
            } else if (c == '}') {
                bracketCount--;
            } else if (c == ',' && bracketCount == 0) {
                result.add(content.substring(start, i).trim());
                start = i + 1;
            }
        }
        
        // 添加最后一个元素
        if (start < content.length()) {
            result.add(content.substring(start).trim());
        }
        
        return result;
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