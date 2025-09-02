package org.example.mobile.xml;

import org.example.common.model.UIElement;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.example.mobile.xml.WdaCleanTreeBuilder.parseAndClean;

public class DomMergerFull {

    public static void main(String[] args) throws Exception {
        String xml1 = Files.readString(Path.of("/Users/snap/workspace/app-agent/merge/1.html"));
        String xml2 = Files.readString(Path.of("/Users/snap/workspace/app-agent/merge/2.html"));

        Document doc1 = parseXml(xml1);
        Document doc2 = parseXml(xml2);

        // 递归收集 existing keys，保留层级
        Set<String> existingKeys = new HashSet<>();
        collectExistingKeys(doc1.getDocumentElement(), existingKeys);

        // 合并 doc2 到 doc1
        mergeDomRecursive(doc1, doc1.getDocumentElement(), doc2.getDocumentElement(), existingKeys);

        // 输出结果
        System.out.println(toString(doc1));

        // 假设 parseAndClean 和 UIElementHtmlRenderer 已存在
        UIElement cleanTree = parseAndClean(toString(doc1));
        String html = UIElementHtmlRenderer.toHtml(cleanTree, 1.0f);
        Files.write(Paths.get("ui-prototype" + System.currentTimeMillis() + ".html"),
                html.getBytes(StandardCharsets.UTF_8));
    }

    public static String merge(String xml1, String xml2) throws Exception {
        Document doc1 = parseXml(xml1);
        Document doc2 = parseXml(xml2);

        // 递归收集 existing keys，保留层级
        Set<String> existingKeys = new HashSet<>();
        collectExistingKeys(doc1.getDocumentElement(), existingKeys);
        mergeDomRecursive(doc1, doc1.getDocumentElement(), doc2.getDocumentElement(), existingKeys);

        // 输出结果
        return toString(doc1);
    }

    // ------------------ DOM 合并 ------------------

    private static void mergeDomRecursive(Document targetDoc, Element targetParent, Element newElem,
                                          Set<String> existingKeys) {

        String key = getUniqueKey(newElem);
        Element importedParent = null;

        if (!existingKeys.contains(key)) {
            // 父节点不存在，需要插入
            double parentY = targetParent.hasAttribute("y") ? Double.parseDouble(targetParent.getAttribute("y")) : 0;
            double elemY = newElem.hasAttribute("y") ? Double.parseDouble(newElem.getAttribute("y")) : 0;
            double yOffset = parentY - elemY;

            adjustCoordinatesRecursive(newElem, yOffset);

            importedParent = (Element) targetDoc.importNode(newElem, false); // 先不拷贝子节点

            Node referenceNode = findInsertionPoint(targetParent, newElem);
            if (referenceNode != null) {
                targetParent.insertBefore(importedParent, referenceNode);
            } else {
                targetParent.appendChild(importedParent);
            }

            double newMaxY = getElementMaxY(importedParent);
            updateAncestorHeights(targetParent, newMaxY);

            existingKeys.add(key);

        } else {
            // 父节点已存在，不插入，但需要找到 targetParent 下对应节点
            importedParent = findChildByKey(targetParent, key);
        }

        if (importedParent == null) return;

        // 递归处理子节点
        NodeList children = newElem.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeType() != Node.ELEMENT_NODE) continue;
            mergeDomRecursive(targetDoc, importedParent, (Element) child, existingKeys);
        }
    }


    private static Element findChildByKey(Element parent, String key) {
        if (getUniqueKey(parent).equalsIgnoreCase(key)) {
            return parent;
        }
        NodeList children = parent.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node node = children.item(i);
            if (node.getNodeType() != Node.ELEMENT_NODE) continue;
            Element e = (Element) node;
            if (getUniqueKey(e).equals(key)) return e;
        }
        return null;
    }

    private static Node findInsertionPoint(Element parent, Element newElem) {
        double newY = newElem.hasAttribute("y") ? Double.parseDouble(newElem.getAttribute("y")) : 0;
        NodeList children = parent.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeType() != Node.ELEMENT_NODE) continue;
            Element childElem = (Element) child;
            double childY = childElem.hasAttribute("y") ? Double.parseDouble(childElem.getAttribute("y")) : 0;
            if (childY > newY) return child;
        }
        return null;
    }

    private static void adjustCoordinatesRecursive(Element elem, double yOffset) {
        if (elem.hasAttribute("y")) {
            double y = Double.parseDouble(elem.getAttribute("y"));
            elem.setAttribute("y", String.valueOf(y + yOffset));
        }
        NodeList children = elem.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                adjustCoordinatesRecursive((Element) child, yOffset);
            }
        }
    }

    private static double getElementMaxY(Node node) {
        double maxY = 0;
        if (node instanceof Element) {
            Element elem = (Element) node;
            if (elem.hasAttribute("y") && elem.hasAttribute("height")) {
                double y = Double.parseDouble(elem.getAttribute("y"));
                double h = Double.parseDouble(elem.getAttribute("height"));
                maxY = y + h;
            }
        }
        NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            maxY = Math.max(maxY, getElementMaxY(children.item(i)));
        }
        return maxY;
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
            } else break;
        }
    }

    // ------------------ 去重 ------------------

    private static void collectExistingKeys(Element elem, Set<String> keys) {
        keys.add(getUniqueKey(elem));
        NodeList children = elem.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node node = children.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                collectExistingKeys((Element) node, keys);
            }
        }
    }

    private static String getUniqueKey(Element elem) {
        StringBuilder sb = new StringBuilder();
        if (elem.getParentNode() instanceof Element) {
            sb.append(getUniqueKey((Element) elem.getParentNode()));
        }
        sb.append("/").append(elem.getTagName());
        List<String> attrs = new ArrayList<>();
        if (elem.hasAttribute("name")) attrs.add("@name='" + elem.getAttribute("name") + "'");
        if (elem.hasAttribute("label")) attrs.add("@label='" + elem.getAttribute("label") + "'");
        if (!attrs.isEmpty()) sb.append("[").append(String.join(" and ", attrs)).append("]");
        return sb.toString();
    }

    // ------------------ XML 工具 ------------------

    public static Document parseXml(String xml) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(false);
        factory.setValidating(false);
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
}
