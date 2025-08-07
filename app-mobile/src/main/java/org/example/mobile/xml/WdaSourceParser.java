package org.example.mobile.xml;

import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.StringReader;
import org.xml.sax.InputSource;

public class WdaSourceParser {

    public static UIElement parse(String xml) {
        try {
            // 清理 BOM 和多余字符
            String cleanXml = xml.trim().replaceFirst("^\\uFEFF", "");
            if (!cleanXml.startsWith("<")) {
                throw new RuntimeException("Invalid XML from WDA: " + cleanXml);
            }

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setIgnoringComments(true);
            factory.setIgnoringElementContentWhitespace(true);

            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new InputSource(new StringReader(cleanXml)));
            Element rootNode = doc.getDocumentElement();

            return buildTree(rootNode, null);

        } catch (Exception e) {
            throw new RuntimeException("Failed to parse WDA source XML", e);
        }
    }

    private static UIElement buildTree(Element node, UIElement parent) {
        UIElement element = new UIElement();
        element.type = node.getTagName();
        element.name = node.getAttribute("name");
        element.label = node.getAttribute("label");
        element.enabled = "true".equals(node.getAttribute("enabled"));
        element.visible = "true".equals(node.getAttribute("visible"));
        element.accessible = "true".equals(node.getAttribute("accessible"));

        // 解析坐标
        try {
            element.x = Float.parseFloat(node.getAttribute("x"));
            element.y = Float.parseFloat(node.getAttribute("y"));
            element.width = Float.parseFloat(node.getAttribute("width"));
            element.height = Float.parseFloat(node.getAttribute("height"));
        } catch (NumberFormatException ignored) {}

        element.parent = parent;

        // 递归解析子节点
        NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node childNode = children.item(i);
            if (childNode.getNodeType() == Node.ELEMENT_NODE) {
                UIElement childElement = buildTree((Element) childNode, element);
                element.addChild(childElement);
            }
        }

        return element;
    }
}
