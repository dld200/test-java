package org.example.mobile.app;

import java.util.List;

/**
 * AppGraph使用示例，展示如何遍历复杂APP结构数据
 */
public class AppGraphExample {
    
    public static void main(String[] args) {
        // 创建一个复杂的APP结构
        AppGraph appGraph = createComplexAppStructure();
        
        // 列出所有页面
        System.out.println("=== 所有页面 ===");
        List<String> pages = appGraph.listPages();
        for (String page : pages) {
            System.out.println("- " + page);
        }
        
        // 获取特定页面的JSON
        System.out.println("\n=== 登录页面详情 ===");
        String loginPageJson = appGraph.getPage("login_page");
        System.out.println(loginPageJson);
        
        System.out.println("\n=== 主页详情 ===");
        String homePageJson = appGraph.getPage("home_page");
        System.out.println(homePageJson);
        
        // 查找从登录页面开始的所有路径（包含元素信息）
        System.out.println("\n=== 从登录页面到个人资料页面的路径（包含元素信息）===");
        List<String> pathWithElements = appGraph.findPathWithElements("login_page", "profile_page");
        if (pathWithElements != null) {
            System.out.println("1. " + String.join(" -> ", pathWithElements));
        } else {
            System.out.println("未找到路径");
        }
        
        // 查找整个图中的所有路径
        System.out.println("\n=== 整个APP中的所有路径 ===");
        List<List<String>> allPaths = appGraph.findAllPaths();
        printPaths(allPaths);
        
        // 查找整个图中的所有路径（包含元素信息）
        System.out.println("\n=== 整个APP中的所有路径（包含元素信息） ===");
        List<List<String>> allPathsWithElements = appGraph.findAllPathsWithElements();
        printPathsWithElements(allPathsWithElements);
        
        // 查找特定路径
        System.out.println("\n=== 从登录页面到个人资料页面的路径 ===");
        List<String> specificPath = appGraph.findPath("login_page", "profile_page");
        if (specificPath != null) {
            System.out.println(String.join(" -> ", specificPath));
        } else {
            System.out.println("未找到路径");
        }
        
        // 检测环路
        System.out.println("\n=== 环路检测 ===");
        System.out.println("是否存在环路: " + appGraph.hasCycle());
        
        // 导出为JSON
        System.out.println("\n=== JSON格式 ===");
        System.out.println(appGraph.toJson());
    }
    
    /**
     * 创建一个复杂的APP结构示例
     * @return AppGraph实例
     */
    private static AppGraph createComplexAppStructure() {
        AppGraph appGraph = new AppGraph();
        
        // 添加页面（使用ID、名称和类型）
        appGraph.addPage("login_page", "登录页面", "stack");
        appGraph.addPage("home_page", "主页", "stack");
        appGraph.addPage("profile_page", "个人资料页面", "stack");
        appGraph.addPage("settings_page", "设置页面", "stack");
        appGraph.addPage("notification_page", "通知页面", "stack");
        appGraph.addPage("search_page", "搜索页面", "stack");
        appGraph.addPage("product_list_page", "产品列表页面", "stack");
        appGraph.addPage("product_detail_page", "产品详情页面", "stack");
        appGraph.addPage("cart_page", "购物车页面", "stack");
        appGraph.addPage("checkout_page", "结算页面", "stack");
        appGraph.addPage("order_confirmation_page", "订单确认页面", "stack");
        appGraph.addPage("help_page", "帮助页面", "stack");
        
        // 为页面添加元素
        // 登录页面元素
        appGraph.addElementToPage("login_page", new Element("username_input", "input", "用户名", "username", 50, 100, 200, 40));
        appGraph.addElementToPage("login_page", new Element("password_input", "input", "密码", "password", 50, 150, 200, 40));
        appGraph.addElementToPage("login_page", new Element("login_btn", "button", "登录", "login_btn", 100, 200, 80, 40, "home_page"));
        
        // 主页元素
        appGraph.addElementToPage("home_page", new Element("welcome_text", "text", "欢迎", "welcome_text", 150, 50, 100, 30));
        appGraph.addElementToPage("home_page", new Element("profile_btn", "button", "个人资料", "profile_btn", 250, 10, 80, 30, "profile_page"));
        appGraph.addElementToPage("home_page", new Element("settings_btn", "button", "设置", "settings_btn", 250, 50, 80, 30, "settings_page"));
        appGraph.addElementToPage("home_page", new Element("search_btn", "button", "搜索", "search_btn", 10, 100, 80, 30, "search_page"));
        appGraph.addElementToPage("home_page", new Element("notification_btn", "button", "通知", "notification_btn", 10, 140, 80, 30, "notification_page"));
        appGraph.addElementToPage("home_page", new Element("product_list_btn", "button", "产品列表", "product_list_btn", 10, 180, 80, 30, "product_list_page"));
        
        // 个人资料页面元素
        appGraph.addElementToPage("profile_page", new Element("profile_title", "text", "个人资料", "profile_title", 150, 50, 100, 30));
        appGraph.addElementToPage("profile_page", new Element("name_input", "input", "姓名", "name_input", 50, 100, 200, 40));
        appGraph.addElementToPage("profile_page", new Element("email_input", "input", "邮箱", "email_input", 50, 150, 200, 40));
        appGraph.addElementToPage("profile_page", new Element("save_btn", "button", "保存", "save_btn", 100, 200, 80, 40));
        appGraph.addElementToPage("profile_page", new Element("settings_btn", "button", "设置", "settings_btn", 10, 10, 80, 30, "settings_page"));
        appGraph.addElementToPage("profile_page", new Element("order_btn", "button", "我的订单", "order_btn", 10, 50, 80, 30, "order_confirmation_page"));
        
        // 设置页面元素
        appGraph.addElementToPage("settings_page", new Element("help_btn", "button", "帮助", "help_btn", 10, 10, 80, 30, "help_page"));
        appGraph.addElementToPage("settings_page", new Element("logout_btn", "button", "退出登录", "logout_btn", 10, 50, 80, 30, "login_page"));
        
        // 通知页面元素
        appGraph.addElementToPage("notification_page", new Element("product_detail_btn", "button", "查看详情", "product_detail_btn", 10, 10, 80, 30, "product_detail_page"));
        
        // 搜索页面元素
        appGraph.addElementToPage("search_page", new Element("product_list_btn", "button", "产品列表", "product_list_btn", 10, 10, 80, 30, "product_list_page"));
        
        // 产品列表页面元素
        appGraph.addElementToPage("product_list_page", new Element("product_detail_btn", "button", "产品详情", "product_detail_btn", 10, 10, 80, 30, "product_detail_page"));
        
        // 产品详情页面元素
        appGraph.addElementToPage("product_detail_page", new Element("cart_btn", "button", "加入购物车", "cart_btn", 10, 10, 80, 30, "cart_page"));
        
        // 购物车页面元素
        appGraph.addElementToPage("cart_page", new Element("checkout_btn", "button", "结算", "checkout_btn", 10, 10, 80, 30, "checkout_page"));
        
        // 结算页面元素
        appGraph.addElementToPage("checkout_page", new Element("confirm_btn", "button", "确认订单", "confirm_btn", 10, 10, 80, 30, "order_confirmation_page"));
        
        // 订单确认页面元素
        appGraph.addElementToPage("order_confirmation_page", new Element("home_btn", "button", "返回首页", "home_btn", 10, 10, 80, 30, "home_page"));
        
        // 帮助页面元素
        appGraph.addElementToPage("help_page", new Element("home_btn", "button", "返回首页", "home_btn", 10, 10, 80, 30, "home_page"));
        
        // 同步邻接表关系（确保targetPageId与邻接表同步）
        appGraph.syncAdjacencyList();
        
        return appGraph;
    }
    
    /**
     * 打印路径列表
     * @param paths 路径列表
     */
    private static void printPaths(List<List<String>> paths) {
        if (paths.isEmpty()) {
            System.out.println("没有找到路径");
            return;
        }
        
        for (int i = 0; i < paths.size(); i++) {
            List<String> path = paths.get(i);
            System.out.println((i + 1) + ". " + String.join(" -> ", path));
        }
        
        System.out.println("总共找到 " + paths.size() + " 条路径");
    }
    
    /**
     * 打印包含元素信息的路径列表
     * @param paths 路径列表
     */
    private static void printPathsWithElements(List<List<String>> paths) {
        if (paths.isEmpty()) {
            System.out.println("没有找到路径");
            return;
        }
        
        for (int i = 0; i < paths.size(); i++) {
            List<String> path = paths.get(i);
            System.out.println((i + 1) + ". " + String.join(" -> ", path));
        }
        
        System.out.println("总共找到 " + paths.size() + " 条路径");
    }
}