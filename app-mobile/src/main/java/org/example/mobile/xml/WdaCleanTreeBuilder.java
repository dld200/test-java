package org.example.mobile.xml;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.data.repository.init.ResourceReader;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class WdaCleanTreeBuilder {

    public static UIElement parseAndClean(String xml) {
        UIElement rawTree = WdaSourceParser.parse(xml);
        return UIElementFilter.clean(rawTree);
    }

    public static void printTree(UIElement node, int depth) {
        if (node == null) return;
        System.out.println("  ".repeat(depth) + node);
        for (UIElement child : node.children) {
            printTree(child, depth + 1);
        }
    }

    public static String toJson(UIElement node) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(node);
    }

    // 测试
    public static void main(String[] args) throws URISyntaxException, IOException {
        String path = ResourceReader.class.getClassLoader().getResource("data.txt").toURI().getPath();

        String xml = new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);

//        UIElement root = WdaSourceParser.parse(xml);

        UIElement cleanTree = parseAndClean(xml);
        printTree(cleanTree, 0);

        String xmlOutput = UIElementXmlSerializer.toXml(cleanTree);
        System.out.println(xmlOutput);

        // 转为 HTML，scale=1.0 表示原尺寸，0.5 表示缩小一半
        String html = UIElementHtmlRenderer.toHtml(cleanTree, 1.0f);
        // 保存到文件
        Files.write(Paths.get("ui-prototype.html"), html.getBytes(StandardCharsets.UTF_8));


        // 添加JSON输出
//        String jsonOutput = toJson(cleanTree);
//        System.out.println("\n=== JSON格式 ===");
//        System.out.println(jsonOutput);
    }
}