package org.example.mobile.automation.source;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.example.mobile.automation.Automation;
import org.example.mobile.automation.IosAutomation;
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

public class UIElementIosParser extends UIElementParser {

    public UiElement parse(String xml) {
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

    @Override
    public  UiElement buildTree(org.w3c.dom.Element node, UiElement parent) {
        UiElement element = new UiElement();
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
                UiElement childElement = buildTree((org.w3c.dom.Element) childNode, element);
                element.addChild(childElement);
            }
        }

        return element;
    }

    public static void main(String[] args) throws Exception {
        String path = ResourceReader.class.getClassLoader().getResource("data.txt").toURI().getPath();

//        String xml = new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);

        Automation automation = new IosAutomation();
        automation.launch("F0F99D79-FCB0-45C3-AD55-89CCCA9BDBFD", "ca.snappay.snaplii.test");

        String xml = automation.source();
        //        String xml = getXmlWithoutExceedingElements(automation, 5);
        UIElementParser x = new UIElementIosParser();

        UiElement cleanTree = x.parseAndClean(xml);
        x.printTree(cleanTree, 0);
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
