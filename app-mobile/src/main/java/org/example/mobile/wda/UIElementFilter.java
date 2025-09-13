package org.example.mobile.wda;

import org.example.common.model.UIElement;

import java.util.ArrayList;
import java.util.List;

public class UIElementFilter {

    /**
     * 主方法：先过滤不可见不可用，再删父子同 bounds
     */
    public static void clean(UIElement element) {
        if (element == null) return;
        List<UIElement> childrenCopy = new ArrayList<>(element.children);
        for (UIElement child : childrenCopy) {
            clean(child); // 递归先简化子节点
        }
        UIElement parent = element.parent;
        if (parent != null) {
            if (element.type.equals("XCUIElementTypeKeyboard")) {
                parent.children.remove(element);
                element.children.clear();
                return;
            }
            if (!isValid(element) || element.eliminate(parent)) {
                // 把 element 的子元素挂到 parent 上
                for (UIElement child : element.children) {
                    child.parent = parent;
                    parent.children.add(child);
                }
                // 从 parent 中移除 element
                parent.children.remove(element);
                element.children.clear();
            }
        }
    }

    private static boolean isValid(UIElement node) {
        return node.y > 837 && node.accessible || node.y < 837 && node.visible && node.accessible;
    }
}
