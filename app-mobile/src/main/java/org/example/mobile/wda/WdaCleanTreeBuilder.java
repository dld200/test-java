package org.example.mobile.wda;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.example.common.model.UIElement;
import org.example.mobile.device.Automation;
import org.example.mobile.device.impl.IosSimulatorAutomation;
import org.springframework.data.repository.init.ResourceReader;
import org.springframework.util.StringUtils;
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
import java.nio.file.Paths;
import java.util.List;

public class WdaCleanTreeBuilder {

    public static UIElement parseAndClean(String xml) {
        UIElement rawTree = WdaSourceParser.parse(xml);
        UIElementFilter.clean(rawTree);
        return rawTree;
    }

    public static void printTree(UIElement node, int depth) {
        if (node == null) return;
        System.out.println("  ".repeat(depth) + node);
        for (UIElement child : node.children) {
            printTree(child, depth + 1);
        }
    }

    /**
     * 检查是否有元素超出屏幕边界
     *
     * @param root XML树的根节点
     * @return 如果有元素超出屏幕边界返回true，否则返回false
     */
    public static boolean hasElementsExceedingScreenBounds(UIElement root) {
        if (root == null) return false;

        // 假设屏幕尺寸为常见的iPhone尺寸 (例如 414x896)，实际可以根据设备动态获取
        float screenWidth = 414.0f;
        float screenHeight = 874.0f;

        // 检查当前节点是否超出屏幕边界
        if (root.y < screenHeight && root.y + root.height > screenHeight + 20) {
            return true;
        }

        // 递归检查子节点
        for (UIElement child : root.children) {
            if (hasElementsExceedingScreenBounds(child)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 合并多个 XML String，返回合并后的 XML String
     *
     * @param xmlStrings 多个 XML 段落
     * @return 合并后的 XML
     */
    public static String mergeXml(String... xmlStrings) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();

        // 创建一个新的文档作为最终结果
        Document mergedDoc = builder.newDocument();
        Element root = mergedDoc.createElement("root"); // 统一的根节点
        mergedDoc.appendChild(root);

        for (String xml : xmlStrings) {
            if (xml == null || xml.trim().isEmpty()) continue;

            Document doc = builder.parse(new InputSource(new StringReader(xml)));
            Element docRoot = doc.getDocumentElement();

            // 把每个 XML 根下的子节点搬到 mergedDoc 的 root 下
            NodeList children = docRoot.getChildNodes();
            for (int i = 0; i < children.getLength(); i++) {
                Node importedNode = mergedDoc.importNode(children.item(i), true);
                root.appendChild(importedNode);
            }
        }

        // 转换成字符串
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");

        StringWriter writer = new StringWriter();
        transformer.transform(new DOMSource(mergedDoc), new StreamResult(writer));
        return writer.toString();
    }

    public static String mergeXmlWithYOffset(List<String> xmlStrings, double screenHeight) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();

        // 创建一个新的文档作为合并结果
        Document mergedDoc = builder.newDocument();
        Element root = mergedDoc.createElement("root");
        mergedDoc.appendChild(root);

        double currentOffset = 0.0;

        for (String xml : xmlStrings) {
            if (xml == null || xml.trim().isEmpty()) continue;

            Document doc = builder.parse(new InputSource(new StringReader(xml)));
            Element docRoot = doc.getDocumentElement();

            NodeList children = docRoot.getChildNodes();
            for (int i = 0; i < children.getLength(); i++) {
                Node node = children.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element elem = (Element) node;

                    // 如果有 y 属性，调整它
                    if (elem.hasAttribute("y")) {
                        double y = Double.parseDouble(elem.getAttribute("y"));
                        elem.setAttribute("y", String.valueOf(y + currentOffset));
                    }

                    // 导入到最终文档
                    Node importedNode = mergedDoc.importNode(elem, true);
                    root.appendChild(importedNode);
                }
            }

            // 每合并一段，偏移量增加一个屏幕高度
            currentOffset += screenHeight;
        }

        // 转换成字符串
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");

        StringWriter writer = new StringWriter();
        transformer.transform(new DOMSource(mergedDoc), new StreamResult(writer));
        return writer.toString();
    }

    /**
     * 持续获取XML直到没有元素超出屏幕边界
     *
     * @param automation 自动化对象
     * @param maxRetries 最大重试次数
     * @return 拼接的XML字符串
     */
    public static String getXmlWithoutExceedingElements(Automation automation, int maxRetries) throws Exception {
        String result = "";
        String xml = automation.source();
        int retries = 0;

        while (retries < maxRetries) {
            xml = automation.source();
//            System.out.println(xml);
            if (StringUtils.isEmpty(result)) {
                result = xml;
            } else {
                result = DomMergerFull.merge(result, xml);
            }
            UIElement root = WdaSourceParser.parse(xml);
            if (!hasElementsExceedingScreenBounds(root)) {
                break; // 没有超出边界的元素，退出循环
            }

            // 有超出边界的元素，执行滑动操作
            automation.swipe("up");
            retries++;

            try {
                // 等待一点时间让界面稳定
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        return result;
    }

    // 测试
    public static void main(String[] args) throws Exception {
        String path = ResourceReader.class.getClassLoader().getResource("data.txt").toURI().getPath();

//        String xml = new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);

        Automation automation = new IosSimulatorAutomation();
        automation.setup("F0F99D79-FCB0-45C3-AD55-89CCCA9BDBFD", "ca.snappay.snaplii.test");

        String xml = automation.source();
        //        String xml = getXmlWithoutExceedingElements(automation, 5);

        UIElement cleanTree = parseAndClean(xml);
//        printTree(cleanTree, 0);
        // 添加JSON输出
        String jsonOutput = JSON.toJSONString(cleanTree, SerializerFeature.SortField, SerializerFeature.PrettyFormat);
//        System.out.println(jsonOutput);

        String xmlOutput = UIElementXmlSerializer.toXml(cleanTree);
        System.out.println(xmlOutput);

        // 转为 HTML，scale=1.0 表示原尺寸，0.5 表示缩小一半
        String html = UIElementHtmlRenderer.toHtml(cleanTree, 1.0f);
        // 保存到文件
        Files.write(Paths.get("ui-prototype" + System.currentTimeMillis() + ".html"), html.getBytes(StandardCharsets.UTF_8));
    }
}