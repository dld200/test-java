package org.example.mobile.xml;

import java.util.ArrayList;
import java.util.List;

public class UIElementFilter {

    /** 主方法：先过滤不可见不可用，再删父子同 bounds */
    public static UIElement clean(UIElement root) {
        if (root == null) return null;

        // 递归处理子节点
        List<UIElement> validChildren = new ArrayList<>();
        for (UIElement child : root.children) {
            UIElement cleanedChild = clean(child);
            if (cleanedChild != null) {
                validChildren.add(cleanedChild);
            }
        }
        root.children = validChildren;

        // 过滤不可见/不可交互节点
        if (!isValid(root) && root.children.isEmpty()) {
            return null;
        }

        // 检查父子同 bounds
        for (UIElement child : root.children) {
            if (root.sameBounds(child)) {
                // 返回子节点，等于删除父节点
                child.parent = root.parent;
                return child;
            }
        }

        return root;
    }

    private static boolean isValid(UIElement node) {
        return node.visible && node.enabled && node.accessible;
    }
}
