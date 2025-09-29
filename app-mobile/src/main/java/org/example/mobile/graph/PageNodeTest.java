package org.example.mobile.graph;

public class PageNodeTest {
    public static void main(String[] args) {
        // 测试Element的JSON解析
        String elementJson = "{\"id\":\"login_btn\",\"type\":\"button\",\"text\":\"登录\",\"label\":\"login_btn\",\"x\":100,\"y\":200,\"width\":80,\"height\":40,\"targetPageId\":\"home_page\"}";
        AppElement element = new AppElement(elementJson);
        
        System.out.println("Element解析结果:");
        System.out.println("ID: " + element.getId());
        System.out.println("Type: " + element.getType());
        System.out.println("Text: " + element.getText());
        System.out.println("Label: " + element.getLabel());
        System.out.println("X: " + element.getX());
        System.out.println("Y: " + element.getY());
        System.out.println("Width: " + element.getWidth());
        System.out.println("Height: " + element.getHeight());
        System.out.println("TargetPageId: " + element.getTargetPageId());
        
        // 测试PageNode的JSON解析
        String pageJson = "{ \"id\":\"login_page\", \"name\":\"登录页面\", \"type\":\"stack\", \"elements\":[ {\"id\":\"username_input\",\"type\":\"input\",\"text\":\"用户名\",\"label\":\"username\",\"x\":50,\"y\":100,\"width\":200,\"height\":40}, {\"id\":\"password_input\",\"type\":\"input\",\"text\":\"密码\",\"label\":\"password\",\"x\":50,\"y\":150,\"width\":200,\"height\":40}, {\"id\":\"login_btn\",\"type\":\"button\",\"text\":\"登录\",\"label\":\"login_btn\",\"x\":100,\"y\":200,\"width\":80,\"height\":40,\"targetPageId\":\"home_page\"} ] }";
        PageNode pageNode = new PageNode(pageJson);
        
        System.out.println("\nPageNode解析结果:");
        System.out.println("ID: " + pageNode.getId());
        System.out.println("Name: " + pageNode.getName());
        System.out.println("Type: " + pageNode.getType());
        System.out.println("Elements count: " + pageNode.getElements().size());
        
        for (int i = 0; i < pageNode.getElements().size(); i++) {
            AppElement e = pageNode.getElements().get(i);
            System.out.println("Element " + i + ": " + e.getId() + " (" + e.getType() + ") - " + e.getText());
        }
    }
}