//package org.example.auto.device;
//
//import java.io.*;
//import java.net.HttpURLConnection;
//import java.net.URL;
//import java.nio.charset.StandardCharsets;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.UUID;
//import com.fasterxml.jackson.databind.ObjectMapper;
//
///**
// * WebDriverAgent工具类
// * 用于与WebDriverAgent进行通信，控制iOS设备进行自动化测试
// */
//public class WebDriverAgent {
//    private static final String DEFAULT_HOST = "localhost";
//    private static final int DEFAULT_PORT = 8100;
//    private static final ObjectMapper objectMapper = new ObjectMapper();
//
//    private String host;
//    private int port;
//    private String sessionId;
//    private boolean connected = false;
//
//    /**
//     * 默认构造函数，使用默认的主机和端口
//     */
//    public WebDriverAgent() {
//        this(DEFAULT_HOST, DEFAULT_PORT);
//    }
//
//    /**
//     * 构造函数
//     * @param host WebDriverAgent服务器主机地址
//     * @param port WebDriverAgent服务器端口
//     */
//    public WebDriverAgent(String host, int port) {
//        this.host = host;
//        this.port = port;
//    }
//
//    /**
//     * 创建会话
//     * @param capabilities 设备和应用的配置信息
//     * @return 会话ID
//     * @throws IOException 网络连接异常
//     */
//    public String createSession(Map<String, Object> capabilities) throws IOException {
//        String url = String.format("http://%s:%d/session", host, port);
//
//        Map<String, Object> requestBody = new HashMap<>();
//        requestBody.put("capabilities", capabilities);
//
//        String response = sendPostRequest(url, requestBody);
//        Map<String, Object> jsonResponse = objectMapper.readValue(response, Map.class);
//
//        Map<String, Object> value = (Map<String, Object>) jsonResponse.get("value");
//        if (value != null) {
//            sessionId = (String) value.get("sessionId");
//            if (sessionId != null) {
//                connected = true;
//            }
//        }
//
//        return sessionId;
//    }
//
//    /**
//     * 删除会话
//     * @throws IOException 网络连接异常
//     */
//    public void deleteSession() throws IOException {
//        if (sessionId == null) {
//            return;
//        }
//
//        String url = String.format("http://%s:%d/session/%s", host, port, sessionId);
//        sendDeleteRequest(url);
//        sessionId = null;
//        connected = false;
//    }
//
//    /**
//     * 查找元素
//     * @param strategy 查找策略 (如 "name", "id", "xpath" 等)
//     * @param selector 选择器
//     * @return 元素信息
//     * @throws IOException 网络连接异常
//     */
//    public Map<String, Object> findElement(String strategy, String selector) throws IOException {
//        checkConnection();
//
//        String url = String.format("http://%s:%d/session/%s/element", host, port, sessionId);
//
//        Map<String, Object> requestBody = new HashMap<>();
//        requestBody.put("using", strategy);
//        requestBody.put("value", selector);
//
//        String response = sendPostRequest(url, requestBody);
//        return objectMapper.readValue(response, Map.class);
//    }
//
//    /**
//     * 点击元素
//     * @param elementId 元素ID
//     * @throws IOException 网络连接异常
//     */
//    public void clickElement(String elementId) throws IOException {
//        checkConnection();
//
//        String url = String.format("http://%s:%d/session/%s/element/%s/click", host, port, sessionId, elementId);
//
//        Map<String, Object> requestBody = new HashMap<>();
//        requestBody.put("element", elementId);
//
//        sendPostRequest(url, requestBody);
//    }
//
//    /**
//     * 输入文本到元素
//     * @param elementId 元素ID
//     * @param text 要输入的文本
//     * @throws IOException 网络连接异常
//     */
//    public void sendKeys(String elementId, String text) throws IOException {
//        checkConnection();
//
//        String url = String.format("http://%s:%d/session/%s/element/%s/value", host, port, sessionId, elementId);
//
//        Map<String, Object> requestBody = new HashMap<>();
//        requestBody.put("text", text);
//        requestBody.put("value", text.split(""));
//
//        sendPostRequest(url, requestBody);
//    }
//
//    /**
//     * 获取设备屏幕截图
//     * @return 截图的Base64编码字符串
//     * @throws IOException 网络连接异常
//     */
//    public String takeScreenshot() throws IOException {
//        checkConnection();
//
//        String url = String.format("http://%s:%d/session/%s/screenshot", host, port, sessionId);
//        String response = sendGetRequest(url);
//
//        Map<String, Object> jsonResponse = objectMapper.readValue(response, Map.class);
//        return (String) jsonResponse.get("value");
//    }
//
//    /**
//     * 执行触摸操作
//     * @param actions 触摸动作列表
//     * @throws IOException 网络连接异常
//     */
//    public void performTouchActions(Map<String, Object> actions) throws IOException {
//        checkConnection();
//
//        String url = String.format("http://%s:%d/session/%s/touch/perform", host, port, sessionId);
//        sendPostRequest(url, actions);
//    }
//
//    /**
//     * 滑动屏幕
//     * @param fromX 起始X坐标
//     * @param fromY 起始Y坐标
//     * @param toX 目标X坐标
//     * @param toY 目标Y坐标
//     * @param duration 滑动持续时间(毫秒)
//     * @throws IOException 网络连接异常
//     */
//    public void swipe(int fromX, int fromY, int toX, int toY, int duration) throws IOException {
//        checkConnection();
//
//        String url = String.format("http://%s:%d/session/%s/wda/dragfromtoforduration", host, port, sessionId);
//
//        Map<String, Object> requestBody = new HashMap<>();
//        requestBody.put("fromX", fromX);
//        requestBody.put("fromY", fromY);
//        requestBody.put("toX", toX);
//        requestBody.put("toY", toY);
//        requestBody.put("duration", duration / 1000.0); // 转换为秒
//
//        sendPostRequest(url, requestBody);
//    }
//
//    /**
//     * 点击屏幕坐标
//     * @param x X坐标
//     * @param y Y坐标
//     * @throws IOException 网络连接异常
//     */
//    public void tap(int x, int y) throws IOException {
//        checkConnection();
//
//        String url = String.format("http://%s:%d/session/%s/wda/tap/0", host, port, sessionId);
//
//        Map<String, Object> requestBody = new HashMap<>();
//        requestBody.put("x", x);
//        requestBody.put("y", y);
//
//        sendPostRequest(url, requestBody);
//    }
//
//    /**
//     * 获取设备状态
//     * @return 设备状态信息
//     * @throws IOException 网络连接异常
//     */
//    public Map<String, Object> getStatus() throws IOException {
//        String url = String.format("http://%s:%d/status", host, port);
//        String response = sendGetRequest(url);
//        return objectMapper.readValue(response, Map.class);
//    }
//
//    /**
//     * 检查连接状态
//     */
//    private void checkConnection() {
//        if (!connected || sessionId == null) {
//            throw new IllegalStateException("Not connected to WebDriverAgent. Please create a session first.");
//        }
//    }
//
//    /**
//     * 发送GET请求
//     * @param urlString 请求URL
//     * @return 响应内容
//     * @throws IOException 网络连接异常
//     */
//    private String sendGetRequest(String urlString) throws IOException {
//        URL url = new URL(urlString);
//        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//        connection.setRequestMethod("GET");
//        connection.setRequestProperty("Content-Type", "application/json; utf-8");
//        connection.setRequestProperty("Accept", "application/json");
//
//        return readResponse(connection);
//    }
//
//    /**
//     * 发送POST请求
//     * @param urlString 请求URL
//     * @param requestBody 请求体
//     * @return 响应内容
//     * @throws IOException 网络连接异常
//     */
//    private String sendPostRequest(String urlString, Map<String, Object> requestBody) throws IOException {
//        URL url = new URL(urlString);
//        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//        connection.setRequestMethod("POST");
//        connection.setRequestProperty("Content-Type", "application/json; utf-8");
//        connection.setRequestProperty("Accept", "application/json");
//        connection.setDoOutput(true);
//
//        String jsonInputString = objectMapper.writeValueAsString(requestBody);
//
//        try (OutputStream os = connection.getOutputStream()) {
//            byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
//            os.write(input, 0, input.length);
//        }
//
//        return readResponse(connection);
//    }
//
//    /**
//     * 发送DELETE请求
//     * @param urlString 请求URL
//     * @return 响应内容
//     * @throws IOException 网络连接异常
//     */
//    private String sendDeleteRequest(String urlString) throws IOException {
//        URL url = new URL(urlString);
//        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//        connection.setRequestMethod("DELETE");
//        connection.setRequestProperty("Content-Type", "application/json; utf-8");
//        connection.setRequestProperty("Accept", "application/json");
//
//        return readResponse(connection);
//    }
//
//    /**
//     * 读取HTTP响应
//     * @param connection HTTP连接
//     * @return 响应内容
//     * @throws IOException 网络连接异常
//     */
//    private String readResponse(HttpURLConnection connection) throws IOException {
//        int responseCode = connection.getResponseCode();
//
//        InputStream inputStream;
//        if (responseCode >= 200 && responseCode < 300) {
//            inputStream = connection.getInputStream();
//        } else {
//            inputStream = connection.getErrorStream();
//        }
//
//        StringBuilder response = new StringBuilder();
//        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
//            String line;
//            while ((line = reader.readLine()) != null) {
//                response.append(line);
//            }
//        }
//
//        return response.toString();
//    }
//
//    /**
//     * 获取会话ID
//     * @return 会话ID
//     */
//    public String getSessionId() {
//        return sessionId;
//    }
//
//    /**
//     * 检查是否已连接
//     * @return 连接状态
//     */
//    public boolean isConnected() {
//        return connected;
//    }
//
//    /**
//     * 获取WebDriverAgent服务器地址
//     * @return 服务器地址
//     */
//    public String getHost() {
//        return host;
//    }
//
//    /**
//     * 获取WebDriverAgent服务器端口
//     * @return 服务器端口
//     */
//    public int getPort() {
//        return port;
//    }
//}