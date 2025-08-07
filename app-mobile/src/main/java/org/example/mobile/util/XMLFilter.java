package org.example.mobile.util;

import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.*;

/**
 * XML过滤器，用于保留具有特定属性的节点
 */
public class XMLFilter {

    /**
     * 解析XML并保留具有enabled="true" visible="true" accessible="true"的节点
     * @param xmlContent XML内容
     * @return 过滤后的XML字符串
     */
    public static String filterXML(String xmlContent) {
        try {
            // 解析XML文档
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new ByteArrayInputStream(xmlContent.getBytes()));

            // 过滤节点
            filterNodes(document.getDocumentElement());

            // 转换为字符串
            return documentToString(document);
        } catch (Exception e) {
            e.printStackTrace();
            return xmlContent;
        }
    }

    /**
     * 递归过滤节点
     * @param node 当前节点
     */
    private static void filterNodes(Node node) {
        // 收集需要处理的子节点
        List<Node> childNodes = new ArrayList<>();
        NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            childNodes.add(children.item(i));
        }

        // 处理每个子节点
        for (Node child : childNodes) {
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) child;

                // 检查是否具有所需的属性
                boolean isEnabled = "true".equals(element.getAttribute("enabled"));
                boolean isVisible = "true".equals(element.getAttribute("visible"));
                boolean isAccessible = "true".equals(element.getAttribute("accessible"));

                // 如果不满足条件，则移除该节点
                if (!(isEnabled && isVisible && isAccessible)) {
                    // 递归处理孙节点
                    List<Node> grandChildren = new ArrayList<>();
                    NodeList grandChildrenList = child.getChildNodes();
                    for (int i = 0; i < grandChildrenList.getLength(); i++) {
                        grandChildren.add(grandChildrenList.item(i));
                    }

                    // 将满足条件的孙节点提升到当前层级
                    for (Node grandChild : grandChildren) {
                        if (grandChild.getNodeType() == Node.ELEMENT_NODE) {
                            Element grandChildElement = (Element) grandChild;
                            boolean isGrandChildEnabled = "true".equals(grandChildElement.getAttribute("enabled"));
                            boolean isGrandChildVisible = "true".equals(grandChildElement.getAttribute("visible"));
                            boolean isGrandChildAccessible = "true".equals(grandChildElement.getAttribute("accessible"));

                            if (isGrandChildEnabled && isGrandChildVisible && isGrandChildAccessible) {
                                // 只有满足条件的孙节点才提升
                                Node movedNode = child.removeChild(grandChild);
                                node.appendChild(movedNode);
                            }
                        } else if (grandChild.getNodeType() == Node.TEXT_NODE) {
                            // 保留文本节点
                            Node movedNode = child.removeChild(grandChild);
                            node.appendChild(movedNode);
                        }
                    }

                    // 移除当前不满足条件的节点
                    node.removeChild(child);
                } else {
                    // 如果满足条件，继续递归处理其子节点
                    filterNodes(child);
                }
            }
        }
    }

    /**
     * 将Document转换为字符串
     * @param document Document对象
     * @return XML字符串
     */
    private static String documentToString(Document document) {
        try {
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");

            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(document), new StreamResult(writer));

            return writer.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 查找具有指定属性的元素
     * @param xmlContent XML内容
     * @return 满足条件的元素列表
     */
    public static List<Map<String, String>> findElementsWithAttributes(String xmlContent) {
        List<Map<String, String>> result = new ArrayList<>();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new ByteArrayInputStream(xmlContent.getBytes()));

            findElements(document.getDocumentElement(), result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 递归查找满足条件的元素
     * @param element 当前元素
     * @param result 结果列表
     */
    private static void findElements(Element element, List<Map<String, String>> result) {
        // 检查元素是否具有所需的属性
        boolean isEnabled = "true".equals(element.getAttribute("enabled"));
        boolean isVisible = "true".equals(element.getAttribute("visible"));
        boolean isAccessible = "true".equals(element.getAttribute("accessible"));

        if (isEnabled && isVisible && isAccessible) {
            Map<String, String> elementInfo = new HashMap<>();
            elementInfo.put("tag", element.getTagName());
            elementInfo.put("name", element.getAttribute("name"));
            elementInfo.put("label", element.getAttribute("label"));
            elementInfo.put("type", element.getAttribute("type"));
            elementInfo.put("x", element.getAttribute("x"));
            elementInfo.put("y", element.getAttribute("y"));
            elementInfo.put("width", element.getAttribute("width"));
            elementInfo.put("height", element.getAttribute("height"));
            result.add(elementInfo);
        }

        // 处理子元素
        NodeList children = element.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                findElements((Element) child, result);
            }
        }
    }

    /**
     * 简化版过滤方法 - 只保留满足条件的节点及其子树
     * @param xmlContent XML内容
     * @return 过滤后的XML字符串
     */
    public static String filterXMLSimple(String xmlContent) {
        try {
            // 解析XML文档
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new ByteArrayInputStream(xmlContent.getBytes()));

            // 过滤节点
            filterNodesSimple(document.getDocumentElement());

            // 转换为字符串
            return documentToString(document);
        } catch (Exception e) {
            e.printStackTrace();
            return xmlContent;
        }
    }

    /**
     * 简化版节点过滤 - 直接移除不满足条件的整个子树
     * @param node 当前节点
     */
    private static void filterNodesSimple(Node node) {
        List<Node> nodesToRemove = new ArrayList<>();

        // 先检查所有子节点
        NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) child;

                // 检查是否具有所需的属性
                boolean isEnabled = "true".equals(element.getAttribute("enabled"));
                boolean isVisible = "true".equals(element.getAttribute("visible"));
                boolean isAccessible = "true".equals(element.getAttribute("accessible"));

                if (isEnabled && isVisible && isAccessible) {
                    // 如果满足条件，继续处理其子节点
                    filterNodesSimple(child);
                } else {
                    // 不满足条件，标记为待删除
                    nodesToRemove.add(child);
                }
            }
        }

        // 删除不满足条件的节点
        for (Node nodeToRemove : nodesToRemove) {
            node.removeChild(nodeToRemove);
        }
    }
}