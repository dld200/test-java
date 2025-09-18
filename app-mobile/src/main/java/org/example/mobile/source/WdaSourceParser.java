package org.example.mobile.source;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.example.mobile.automation.Automation;
import org.example.mobile.automation.IosSimulatorAutomation;
import org.example.mobile.automation.Element;
import org.springframework.data.repository.init.ResourceReader;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class WdaSourceParser {

    public static Element parse(String xml) {
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
            org.w3c.dom.Element rootNode = doc.getDocumentElement();

            return buildTree(rootNode, null);

        } catch (Exception e) {
            throw new RuntimeException("Failed to parse WDA source XML", e);
        }
    }

    private static Element buildTree(org.w3c.dom.Element node, Element parent) {
        Element element = new Element();
        element.type = node.getTagName();
        element.name = node.getAttribute("name");
        element.label = node.getAttribute("label");
        element.enabled = "true".equals(node.getAttribute("enabled"));
        element.visible = "true".equals(node.getAttribute("visible"));
        element.accessible = "true".equals(node.getAttribute("accessible"));

        // 解析坐标
        try {
            element.x = Integer.parseInt(node.getAttribute("x"));
            element.y = Integer.parseInt(node.getAttribute("y"));
            element.width = Integer.parseInt(node.getAttribute("width"));
            element.height = Integer.parseInt(node.getAttribute("height"));
        } catch (NumberFormatException ignored) {
        }

        element.parent = parent;

        // 递归解析子节点
        NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node childNode = children.item(i);
            if (childNode.getNodeType() == Node.ELEMENT_NODE) {
                Element childElement = buildTree((org.w3c.dom.Element) childNode, element);
                element.addChild(childElement);
            }
        }

        return element;
    }

    public static void clean(Element element) {
        if (element == null) return;
        List<Element> childrenCopy = new ArrayList<>(element.children);
        for (Element child : childrenCopy) {
            clean(child); // 递归先简化子节点
        }
        Element parent = element.parent;
        if (parent != null) {
            if (element.type.equals("XCUIElementTypeKeyboard")) {
                parent.children.remove(element);
                element.children.clear();
                return;
            }
            if (!isValid(element) || element.eliminate(parent)) {
                // 把 element 的子元素挂到 parent 上
                for (Element child : element.children) {
                    child.parent = parent;
                    parent.children.add(child);
                }
                // 从 parent 中移除 element
                parent.children.remove(element);
                element.children.clear();
            }
        }
    }

    private static boolean isValid(Element node) {
        return node.y > 837 && node.accessible || node.y < 837 && node.visible && node.accessible;
    }

    public static Element parseAndClean(String xml) {
        Element rawTree = WdaSourceParser.parse(xml);
        WdaSourceParser.clean(rawTree);
        return rawTree;
    }

    public static void printTree(Element node, int depth) {
        if (node == null) return;
        System.out.println("  ".repeat(depth) + node);
        for (Element child : node.children) {
            printTree(child, depth + 1);
        }
    }

    public static void main(String[] args) throws Exception {
        String path = ResourceReader.class.getClassLoader().getResource("data.txt").toURI().getPath();

//        String xml = new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);

        Automation automation = new IosSimulatorAutomation();
        automation.setup("F0F99D79-FCB0-45C3-AD55-89CCCA9BDBFD", "ca.snappay.snaplii.test");

        String xml = automation.source();
        //        String xml = getXmlWithoutExceedingElements(automation, 5);

        Element cleanTree = parseAndClean(xml);
        printTree(cleanTree, 0);
        // 添加JSON输出
        String jsonOutput = JSON.toJSONString(cleanTree, SerializerFeature.SortField, SerializerFeature.PrettyFormat);
//        System.out.println(jsonOutput);

        String xmlOutput = UIElementXmlSerializer.toXml(cleanTree);
        System.out.println(xmlOutput);

        // 转为 HTML，scale=1.0 表示原尺寸，0.5 表示缩小一半
        String html = UIElementHtmlSerializer.toHtml(cleanTree, 1.0f);
        // 保存到文件
        Files.write(Paths.get("ui-prototype" + System.currentTimeMillis() + ".html"), html.getBytes(StandardCharsets.UTF_8));
    }
}
