package org.example.mobile.automation.source;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

public abstract class UIElementParser {

    public abstract UiElement parse(String xml);

    public abstract UiElement buildTree(org.w3c.dom.Element node, UiElement parent);

    public  void clean(UiElement element) {
        if (element == null) return;
        List<UiElement> childrenCopy = new ArrayList<>(element.children);
        for (UiElement child : childrenCopy) {
            clean(child); // 递归先简化子节点
        }
        UiElement parent = element.parent;
        if (parent != null) {
            if (element.type.equals("XCUIElementTypeKeyboard")) {
                parent.children.remove(element);
                element.children.clear();
                return;
            }
            if (!isValid(element) || element.eliminate(parent)) {
                // 把 element 的子元素挂到 parent 上
                for (UiElement child : element.children) {
                    child.parent = parent;
                    parent.children.add(child);
                }
                // 从 parent 中移除 element
                parent.children.remove(element);
                element.children.clear();
            }
        }
    }

    private  boolean isValid(UiElement node) {
        return node.y > 837 && node.accessible || node.y < 837 && node.visible && node.accessible;
    }

    public  UiElement parseAndClean(String xml) {
        UiElement rawTree = parse(xml);
        clean(rawTree);
        return rawTree;
    }

    public  void printTree(UiElement node, int depth) {
        if (node == null) return;
        System.out.println("  ".repeat(depth) + node);
        for (UiElement child : node.children) {
            printTree(child, depth + 1);
        }
    }
}
