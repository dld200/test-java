package org.example.mobile.xml;

public class UIElementXmlSerializer {

    public static String toXml(UIElement root) {
        StringBuilder sb = new StringBuilder();
        appendNode(sb, root, 0);
        return sb.toString();
    }

    private static void appendNode(StringBuilder sb, UIElement node, int depth) {
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
            for (UIElement child : node.children) {
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
}
