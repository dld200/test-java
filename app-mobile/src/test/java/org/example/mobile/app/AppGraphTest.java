package org.example.mobile.app;

import org.example.mobile.graph.AppGraph;
import org.example.mobile.graph.Element;
import org.example.mobile.graph.PageNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class AppGraphTest {
    
    private AppGraph appGraph;
    
    @BeforeEach
    public void setUp() {
        appGraph = new AppGraph();
    }
    
    @Test
    public void testAddPage() {
        appGraph.addPage("login_page", "登录页面", "stack");
        
        // 导出为JSON并验证节点存在
        String json = appGraph.toJson();
        assertTrue(json.contains("login_page"));
        assertTrue(json.contains("登录页面"));
    }
    
    @Test
    public void testListPages() {
        // 添加页面
        appGraph.addPage("login_page", "登录页面", "stack");
        appGraph.addPage("home_page", "主页", "stack");
        appGraph.addPage("profile_page", "个人资料页面", "stack");
        
        // 获取页面列表
        List<String> pages = appGraph.listPages();
        assertNotNull(pages);
        assertEquals(3, pages.size());
        assertTrue(pages.contains("login_page"));
        assertTrue(pages.contains("home_page"));
        assertTrue(pages.contains("profile_page"));
    }
    
    @Test
    public void testAddElementToPage() {
        // 添加页面和元素
        appGraph.addPage("login_page", "登录页面", "stack");
        Element element = new Element("login_btn", "button", "登录", "login_btn", 100, 200, 80, 40, "home_page");
        appGraph.addElementToPage("login_page", element);
        
        // 验证元素被正确添加
        String json = appGraph.toJson();
        assertTrue(json.contains("button"));
        assertTrue(json.contains("登录"));
        assertTrue(json.contains("login_btn"));
        assertTrue(json.contains("100"));
        assertTrue(json.contains("200"));
        assertTrue(json.contains("80"));
        assertTrue(json.contains("40"));
        assertTrue(json.contains("home_page"));
    }
    
    @Test
    public void testGetPage() {
        // 添加页面和元素
        appGraph.addPage("login_page", "登录页面", "stack");
        Element element = new Element("login_btn", "button", "登录", "login_btn", 100, 200, 80, 40, "home_page");
        appGraph.addElementToPage("login_page", element);
        
        // 获取页面JSON
        String pageJson = appGraph.getPage("login_page");
        assertNotNull(pageJson);
        assertTrue(pageJson.contains("login_page"));
        assertTrue(pageJson.contains("登录页面"));
        assertTrue(pageJson.contains("button"));
        assertTrue(pageJson.contains("登录"));
        assertTrue(pageJson.contains("login_btn"));
        assertTrue(pageJson.contains("100"));
        assertTrue(pageJson.contains("200"));
        assertTrue(pageJson.contains("80"));
        assertTrue(pageJson.contains("40"));
        assertTrue(pageJson.contains("home_page"));
    }
    
    @Test
    public void testGetPageNotFound() {
        // 尝试获取不存在的页面
        String pageJson = appGraph.getPage("non_existent_page");
        assertNull(pageJson);
    }
    
    @Test
    public void testFindPath() {
        // 构建一个简单的路径: LoginPage -> HomePage -> ProfilePage
        appGraph.addPage("login_page", "登录页面", "stack");
        appGraph.addPage("home_page", "主页", "stack");
        appGraph.addPage("profile_page", "个人资料页面", "stack");
        
        // 通过添加元素建立页面间的关系
        appGraph.addElementToPage("login_page", new Element("login_btn", "button", "登录", "login_btn", 100, 200, 80, 40, "home_page"));
        appGraph.addElementToPage("home_page", new Element("profile_btn", "button", "个人资料", "profile_btn", 250, 10, 80, 30, "profile_page"));
        
        // 测试查找路径
        List<String> path = appGraph.findPath("login_page", "profile_page");
        assertNotNull(path);
        assertEquals(3, path.size());
        assertEquals("login_page", path.get(0));
        assertEquals("home_page", path.get(1));
        assertEquals("profile_page", path.get(2));
    }
    
    @Test
    public void testFindPathWithElements() {
        // 构建一个简单的路径: LoginPage -> HomePage -> ProfilePage
        appGraph.addPage("login_page", "登录页面", "stack");
        appGraph.addPage("home_page", "主页", "stack");
        appGraph.addPage("profile_page", "个人资料页面", "stack");
        
        // 通过添加元素建立页面间的关系
        appGraph.addElementToPage("login_page", new Element("login_btn", "button", "登录", "login_btn", 100, 200, 80, 40, "home_page"));
        appGraph.addElementToPage("home_page", new Element("profile_btn", "button", "个人资料", "profile_btn", 250, 10, 80, 30, "profile_page"));
        
        // 测试查找路径（包含元素信息）
        List<String> path = appGraph.findPathWithElements("login_page", "profile_page");
        assertNotNull(path);
        assertEquals(3, path.size());
        assertEquals("login_page", path.get(0));
        assertEquals("home_page(login_btn)", path.get(1));
        assertEquals("profile_page", path.get(2));
    }
    
    @Test
    public void testFindPathNoPath() {
        // 添加两个不相连的页面
        appGraph.addPage("login_page", "登录页面", "stack");
        appGraph.addPage("settings_page", "设置页面", "stack");
        
        // 通过添加元素建立页面间的关系，但不连接login_page和settings_page
        appGraph.addElementToPage("login_page", new Element("login_btn", "button", "登录", "login_btn", 100, 200, 80, 40));
        appGraph.addElementToPage("settings_page", new Element("setting_btn", "button", "设置", "setting_btn", 100, 200, 80, 40));
        
        // 测试查找不存在的路径
        List<String> path = appGraph.findPath("login_page", "settings_page");
        assertNull(path);
    }
    
    @Test
    public void testFindAllPathsFrom() {
        // 添加页面
        appGraph.addPage("login_page", "登录页面", "stack");
        appGraph.addPage("home_page", "主页", "stack");
        appGraph.addPage("profile_page", "个人资料页面", "stack");
        appGraph.addPage("settings_page", "设置页面", "stack");
        appGraph.addPage("order_confirmation_page", "订单确认页面", "stack");
        
        // 通过添加元素建立页面间的关系
        appGraph.addElementToPage("login_page", new Element("login_btn", "button", "登录", "login_btn", 100, 200, 80, 40, "home_page"));
        appGraph.addElementToPage("home_page", new Element("profile_btn", "button", "个人资料", "profile_btn", 250, 10, 80, 30, "profile_page"));
        appGraph.addElementToPage("home_page", new Element("settings_btn", "button", "设置", "settings_btn", 250, 50, 80, 30, "settings_page"));
        appGraph.addElementToPage("profile_page", new Element("order_btn", "button", "我的订单", "order_btn", 10, 50, 80, 30, "order_confirmation_page"));
        
        // 测试从LoginPage开始查找所有路径
        List<List<String>> paths = appGraph.findAllPathsFrom("login_page");
        assertNotNull(paths);
        // 应该找到3条路径:
        // 1. login_page -> home_page
        // 2. login_page -> home_page -> profile_page
        // 3. login_page -> home_page -> settings_page
        // 4. login_page -> home_page -> profile_page -> order_confirmation_page
        assertEquals(4, paths.size());
    }
    
    @Test
    public void testFindAllPaths() {
        // 添加页面
        appGraph.addPage("login_page", "登录页面", "stack");
        appGraph.addPage("home_page", "主页", "stack");
        appGraph.addPage("profile_page", "个人资料页面", "stack");
        
        // 通过添加元素建立页面间的关系
        appGraph.addElementToPage("login_page", new Element("login_btn", "button", "登录", "login_btn", 100, 200, 80, 40, "home_page"));
        appGraph.addElementToPage("home_page", new Element("profile_btn", "button", "个人资料", "profile_btn", 250, 10, 80, 30, "profile_page"));
        
        // 测试查找所有路径
        List<List<String>> paths = appGraph.findAllPaths();
        assertNotNull(paths);
        // 至少应该包含以下路径:
        // 1. login_page -> home_page
        // 2. home_page -> profile_page
        assertTrue(paths.size() >= 2);
    }
    
    @Test
    public void testFindAllPathsWithElements() {
        // 添加页面
        appGraph.addPage("login_page", "登录页面", "stack");
        appGraph.addPage("home_page", "主页", "stack");
        appGraph.addPage("profile_page", "个人资料页面", "stack");
        
        // 通过添加元素建立页面间的关系
        appGraph.addElementToPage("login_page", new Element("login_btn", "button", "登录", "login_btn", 100, 200, 80, 40, "home_page"));
        appGraph.addElementToPage("home_page", new Element("profile_btn", "button", "个人资料", "profile_btn", 250, 10, 80, 30, "profile_page"));
        
        // 测试查找所有路径（包含元素信息）
        List<List<String>> paths = appGraph.findAllPathsWithElements();
        assertNotNull(paths);
        // 应该至少包含以下路径:
        // 1. login_page(login_btn) -> home_page
        // 2. login_page(login_btn) -> home_page(profile_btn) -> profile_page
        assertTrue(paths.size() >= 2);
        
        // 验证路径包含元素信息
        boolean foundLoginToHome = false;
        boolean foundFullChain = false;
        for (List<String> path : paths) {
            String pathStr = String.join(" -> ", path);
            if (pathStr.contains("login_page(login_btn)") && pathStr.contains("home_page")) {
                foundLoginToHome = true;
            }
            if (pathStr.contains("login_page(login_btn)") && pathStr.contains("home_page(profile_btn)") && pathStr.contains("profile_page")) {
                foundFullChain = true;
            }
        }
        assertTrue(foundLoginToHome, "应该找到从登录页面到主页的路径");
        assertTrue(foundFullChain, "应该找到完整的路径链");
    }
    
    @Test
    public void testHasCycle() {
        // 添加页面
        appGraph.addPage("page_a", "页面A", "stack");
        appGraph.addPage("page_b", "页面B", "stack");
        appGraph.addPage("page_c", "页面C", "stack");
        
        // 通过添加元素建立页面间的关系，构建一个有环的图: A -> B -> C -> A
        appGraph.addElementToPage("page_a", new Element("btn_b", "button", "到B", "btn_b", 10, 10, 80, 30, "page_b"));
        appGraph.addElementToPage("page_b", new Element("btn_c", "button", "到C", "btn_c", 10, 10, 80, 30, "page_c"));
        appGraph.addElementToPage("page_c", new Element("btn_a", "button", "到A", "btn_a", 10, 10, 80, 30, "page_a"));
        
        // 测试检测环路
        assertTrue(appGraph.hasCycle());
    }
    
    @Test
    public void testHasNoCycle() {
        // 添加页面
        appGraph.addPage("page_a", "页面A", "stack");
        appGraph.addPage("page_b", "页面B", "stack");
        appGraph.addPage("page_c", "页面C", "stack");
        
        // 通过添加元素建立页面间的关系，构建一个无环的图: A -> B -> C
        appGraph.addElementToPage("page_a", new Element("btn_b", "button", "到B", "btn_b", 10, 10, 80, 30, "page_b"));
        appGraph.addElementToPage("page_b", new Element("btn_c", "button", "到C", "btn_c", 10, 10, 80, 30, "page_c"));
        
        // 测试检测环路
        assertFalse(appGraph.hasCycle());
    }
    
    @Test
    public void testToJson() {
        // 添加页面
        appGraph.addPage("login_page", "登录页面", "stack");
        appGraph.addPage("home_page", "主页", "stack");
        appGraph.addPage("profile_page", "个人资料页面", "stack");
        
        // 通过添加元素建立页面间的关系
        appGraph.addElementToPage("login_page", new Element("login_btn", "button", "登录", "login_btn", 100, 200, 80, 40, "home_page"));
        appGraph.addElementToPage("home_page", new Element("profile_btn", "button", "个人资料", "profile_btn", 250, 10, 80, 30, "profile_page"));
        
        // 添加元素
        Element element = new Element("login_btn", "button", "登录", "login_btn", 100, 200, 80, 40, "home_page");
        appGraph.addElementToPage("login_page", element);
        
        // 导出为JSON
        String json = appGraph.toJson();
        
        // 验证JSON格式和内容
        assertTrue(json.startsWith("{"));
        assertTrue(json.endsWith("}"));
        assertTrue(json.contains("\"nodes\""));
        assertTrue(json.contains("\"edges\""));
        assertTrue(json.contains("login_page"));
        assertTrue(json.contains("home_page"));
        assertTrue(json.contains("profile_page"));
        assertTrue(json.contains("button"));
        assertTrue(json.contains("登录"));
        assertTrue(json.contains("home_page"));
        // 验证边信息包含元素ID和名称
        assertTrue(json.contains("\"elementId\":\"login_btn\""));
        assertTrue(json.contains("\"elementName\":\"login_btn\""));
    }
    
    @Test
    public void testSyncAdjacencyList() {
        // 添加页面
        appGraph.addPage("page_a", "页面A", "stack");
        appGraph.addPage("page_b", "页面B", "stack");
        
        // 添加指向关系但不通过addElementToPage方法
        PageNode pageA = new PageNode("page_a", "页面A", "stack");
        pageA.addElement(new Element("btn_b", "button", "到B", "btn_b", 10, 10, 80, 30, "page_b"));
        
        // 手动更新节点
        try {
            java.lang.reflect.Field nodesField = AppGraph.class.getDeclaredField("nodes");
            nodesField.setAccessible(true);
            @SuppressWarnings("unchecked")
            java.util.Map<String, PageNode> nodes = (java.util.Map<String, PageNode>) nodesField.get(appGraph);
            nodes.put("page_a", pageA);
        } catch (Exception e) {
            fail("反射操作失败: " + e.getMessage());
        }
        
        // 同步邻接表
        appGraph.syncAdjacencyList();
        
        // 验证路径是否存在
        List<String> path = appGraph.findPath("page_a", "page_b");
        assertNotNull(path);
        assertEquals(2, path.size());
        assertEquals("page_a", path.get(0));
        assertEquals("page_b", path.get(1));
        
        // 验证JSON中包含元素信息
        String json = appGraph.toJson();
        assertTrue(json.contains("\"elementId\":\"btn_b\""));
        assertTrue(json.contains("\"elementName\":\"btn_b\""));
    }
}