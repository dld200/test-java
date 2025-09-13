package org.example.mobile.wda;

import org.example.common.model.UIElement;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.xml.sax.InputSource;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.example.mobile.wda.WdaCleanTreeBuilder.parseAndClean;

public class XmlMergerWithSpacing {

    /**
     * 插入新 XML 节点，y 坐标基于父元素最后子元素 + spacing
     */
    public static void insertXmlWithSpacing(Document targetDoc, Element parent, Document newDoc, double spacing) {
        Element newRoot = newDoc.getDocumentElement();
        NodeList children = newRoot.getChildNodes();

        // 1. 找到父元素最后一个子元素，计算偏移
        double yOffset = 0;
        NodeList existingChildren = parent.getChildNodes();
        for (int i = existingChildren.getLength() - 1; i >= 0; i--) {
            Node node = existingChildren.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element elem = (Element) node;
                if (elem.hasAttribute("y") && elem.hasAttribute("height")) {
                    double lastY = Double.parseDouble(elem.getAttribute("y"));
                    double lastH = Double.parseDouble(elem.getAttribute("height"));
                    yOffset = lastY + lastH + spacing - parentY(parent);
                    break;
                }
            }
        }

        // 2. 遍历新 XML 子节点，调整 y
        double maxAddedHeight = 0;
        for (int i = 0; i < children.getLength(); i++) {
            Node node = children.item(i);
            if (node.getNodeType() != Node.ELEMENT_NODE) continue;
            Element elem = (Element) node;

            if (elem.hasAttribute("y")) {
                double y = Double.parseDouble(elem.getAttribute("y"));
                double newY = y + yOffset;
                elem.setAttribute("y", String.valueOf(newY));
                double h = elem.hasAttribute("height") ? Double.parseDouble(elem.getAttribute("height")) : 0;
                maxAddedHeight = Math.max(maxAddedHeight, newY + h - parentY(parent));
            }

            Node imported = targetDoc.importNode(elem, true);
            parent.appendChild(imported);
        }

        // 3. 更新父元素及祖先高度
        updateAncestorHeights(parent, maxAddedHeight);
    }

    private static double parentY(Element parent) {
        if (parent.hasAttribute("y")) {
            return Double.parseDouble(parent.getAttribute("y"));
        }
        return 0;
    }

    private static void updateAncestorHeights(Element elem, double totalHeight) {
        while (elem != null) {
            if (elem.hasAttribute("height")) {
                double h = Double.parseDouble(elem.getAttribute("height"));
                elem.setAttribute("height", String.valueOf(Math.max(h, totalHeight)));
            }
            Node parentNode = elem.getParentNode();
            if (parentNode instanceof Element) {
                elem = (Element) parentNode;
            } else {
                break;
            }
        }
    }

    // 工具方法
    public static Document parseXml(String xml) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(new InputSource(new StringReader(xml)));
    }

    public static String toString(Document doc) throws Exception {
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        StringWriter writer = new StringWriter();
        transformer.transform(new DOMSource(doc), new StreamResult(writer));
        return writer.toString();
    }

    // 测试
    public static void main(String[] args) throws Exception {
        String xml1 = Files.readString(Path.of("/Users/snap/workspace/app-agent/merge/1.html"));
        String xml2 = Files.readString(Path.of("/Users/snap/workspace/app-agent/merge/2.html"));

        Document doc1 = parseXml(xml1);
        Document doc2 = parseXml(xml2);

        insertXmlWithSpacing(doc1, doc1.getDocumentElement(), doc2, 10); // 间距10

        String xml =toString(doc1);
        System.out.println(xml);

        UIElement cleanTree = parseAndClean(xml);
        // 转为 HTML，scale=1.0 表示原尺寸，0.5 表示缩小一半
        String html = UIElementHtmlRenderer.toHtml(cleanTree, 1.0f);
        // 保存到文件
        Files.write(Paths.get("ui-prototype" + System.currentTimeMillis() +".html"), html.getBytes(StandardCharsets.UTF_8));


    }
}
