package org.example.mobile.automation.android;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.example.mobile.automation.Automation;
import org.example.mobile.automation.UIElementParser;
import org.example.mobile.automation.UIElementSerializer;
import org.example.mobile.automation.UiElement;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AndroidSourceParser implements UIElementParser {

    private static Pattern pattern = Pattern.compile("\\[(\\d+),(\\d+)\\]\\[(\\d+),(\\d+)\\]");

    @Override
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

    /**
     * <node index="0" text="Save $9.99 with Exclusive Coupon" resource-id="" class="android.widget.TextView"
     * package="ca.snappay.snaplii.test"
     * content-desc="" checkable="false" checked="false" clickable="false" enabled="true" focusable="false"
     * focused="false" scrollable="false" long-clickable="false" password="false" selected="false"
     * bounds="[69,1961][677,2019]" /></node>
     */
    @Override
    public UiElement buildTree(org.w3c.dom.Element node, UiElement parent) {
        UiElement element = new UiElement();
        element.type = node.getAttribute("class");
        element.name = node.getAttribute("content-desc");
        element.label = node.getAttribute("text");
        element.enabled = "true".equals(node.getAttribute("enabled"));
        element.visible = element.enabled;
        element.accessible = element.enabled;
        //"true".equals(node.getAttribute("focusable")) || "true".equals(node.getAttribute("false"));

        // 解析坐标
        Matcher matcher = pattern.matcher(node.getAttribute("bounds"));
        if (matcher.find()) {
            element.x = Integer.parseInt(matcher.group(1));
            element.y = Integer.parseInt(matcher.group(2));
            element.width = Integer.parseInt(matcher.group(3)) - element.x;
            element.height = Integer.parseInt(matcher.group(4)) - element.y;
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
        String deviceId = "53F5T19905000341";
        Automation robot = new AndroidAutomation();
        System.out.println(robot.listDevices());
//        robot.launch(deviceId, "ca.snappay.snaplii.test");
        // 启动应用举例
//        robot.launchApp("ca.snappay.snaplii.test");

        String xml = robot.source();
        System.out.println(xml);
        UIElementParser x = new AndroidSourceParser();

        UiElement cleanTree = x.parseAndClean(xml);
        x.printTree(cleanTree, 0);
        String jsonOutput = JSON.toJSONString(cleanTree, SerializerFeature.SortField, SerializerFeature.PrettyFormat);
//        System.out.println(jsonOutput);

        String xmlOutput = UIElementSerializer.toXml(cleanTree);
        System.out.println(xmlOutput);

        // 转为 HTML，scale=1.0 表示原尺寸，0.5 表示缩小一半
        String html = UIElementSerializer.toHtml(cleanTree, 0.4f);
        // 保存到文件
        Files.write(Paths.get("ui-prototype" + System.currentTimeMillis() + ".html"), html.getBytes(StandardCharsets.UTF_8));

    }
}
