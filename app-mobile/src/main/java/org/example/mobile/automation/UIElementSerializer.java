package org.example.mobile.automation;

public class UIElementSerializer {

    public static String toXml(UiElement root) {
        StringBuilder sb = new StringBuilder();
        appendNode(sb, root, 0);
        return sb.toString();
    }

    private static void appendNode(StringBuilder sb, UiElement node, int depth) {
        if (node == null) return;
        String indent = "  ".repeat(depth);

        // 开始标签
        sb.append(indent)
                .append("<").append(node.type)
                .append(" name=\"").append(escape(node.name)).append("\"")
                .append(" label=\"").append(escape(node.label)).append("\"")
                .append(" enabled=\"").append(node.enabled).append("\"")
                .append(" visible=\"").append(node.visible).append("\"")
                .append(" accessible=\"").append(node.accessible).append("\"")
                .append(" x=\"").append(node.x).append("\"")
                .append(" y=\"").append(node.y).append("\"")
                .append(" width=\"").append(node.width).append("\"")
                .append(" height=\"").append(node.height).append("\"");

        if (node.children.isEmpty()) {
            // 没有子节点，直接闭合
            sb.append("/>\n");
        } else {
            sb.append(">\n");

            // 递归子节点
            for (UiElement child : node.children) {
                appendNode(sb, child, depth + 1);
            }

            // 结束标签
            sb.append(indent).append("</").append(node.type).append(">\n");
        }
    }

    private static String escape(String value) {
        if (value == null) return "";
        return value.replace("&", "&amp;")
                .replace("\"", "&quot;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }

    public static String toHtml(UiElement root, float scale) {
        StringBuilder sb = new StringBuilder();
        sb.append("<!DOCTYPE html>\n<html>\n<head>\n")
                .append("<meta charset=\"UTF-8\">\n")
                .append("<style>\n")
                .append("body { margin:0; background:#f0f0f0; }\n")
                .append("div { border:1px solid #007bff; }\n")
                .append(".screen { position:relative; background:white; transform: scale(0.75); transform-origin: top left;}\n")
                .append(".element { position:absolute; box-sizing:border-box; ")
                .append("font-size:14px; overflow:hidden; word-wrap: break-word; }\n")
                .append(".type-Button { border:1px solid #007bff; background:rgba(0,123,255,0.1); }\n")
                .append(".type-TextField, .type-SecureTextField { border:1px solid #28a745; background:rgba(40,167,69,0.1); }\n")
                .append(".type-StaticText { border:1px dashed #6c757d; background:rgba(108,117,125,0.1); }\n")
                .append(".type-Other { border:1px dashed #ccc; background:rgba(200,200,200,0.05); }\n")
                .append("</style>\n</head>\n<body>\n");

        // 计算整体屏幕大小
        float screenWidth = root.width * scale;
        float screenHeight = root.height * scale;

        sb.append("<div class=\"screen\" style=\"width:")
                .append(screenWidth).append("px;height:")
                .append(screenHeight).append("px;\">\n");

        appendElement(sb, root, scale);

        sb.append("</div>\n</body>\n</html>");

        return sb.toString();
    }

    private static void appendElement(StringBuilder sb, UiElement node, float scale) {
        if (node == null) return;

        float left = node.x * scale;
        float top = node.y * scale;
        float width = node.width * scale;
        float height = node.height * scale;

        // 生成类型 class
        String typeClass = "type-" + node.type.replace("XCUIElementType", "");

        sb.append("<div class=\"element ").append(typeClass).append("\" ")
                .append("style=\"left:").append(left).append("px;top:").append(top)
                .append("px;width:").append(width).append("px;height:").append(height)
                .append("px;\">");

        // 显示 name 或 label
        String text = (node.name != null && !node.name.isEmpty()) ? node.name :
                (node.label != null && !node.label.isEmpty()) ? node.label : "";
        if (node.accessible && !text.isEmpty()) {
            sb.append(escape(text));
        }

        sb.append("</div>\n");

        // 递归子元素
        for (UiElement child : node.children) {
            appendElement(sb, child, scale);
        }
    }

}
