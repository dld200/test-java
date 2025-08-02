package org.example.mobile.test;

import java.util.*;

public class AppGraph {
    private Map<String, PageNode> nodes = new HashMap<>();
    private Map<String, List<Edge>> adjacencyList = new HashMap<>();
    
    /**
     * 边类，用于存储页面间的关系，包括目标页面和触发跳转的元素
     */
    private static class Edge {
        private final String targetPageId;
        private final String elementId;
        private final String elementName;
        
        public Edge(String targetPageId, String elementId, String elementName) {
            this.targetPageId = targetPageId;
            this.elementId = elementId;
            this.elementName = elementName;
        }
        
        public String getTargetPageId() {
            return targetPageId;
        }
        
        public String getElementId() {
            return elementId;
        }
        
        public String getElementName() {
            return elementName;
        }
        
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Edge edge = (Edge) o;
            return Objects.equals(targetPageId, edge.targetPageId) &&
                   Objects.equals(elementId, edge.elementId) &&
                   Objects.equals(elementName, edge.elementName);
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(targetPageId, elementId, elementName);
        }
    }
    
    /**
     * 路径节点类，用于存储路径查找过程中的页面和元素信息
     */
    private static class PathNode {
        private final String pageId;
        private final String elementId;
        
        public PathNode(String pageId, String elementId) {
            this.pageId = pageId;
            this.elementId = elementId;
        }
        
        public String getPageId() {
            return pageId;
        }
        
        public String getElementId() {
            return elementId;
        }
    }
    
    public void addPage(String id, String type) {
        nodes.putIfAbsent(id, new PageNode(id, type));
        adjacencyList.putIfAbsent(id, new ArrayList<>());
    }

    public void addPage(String id, String name, String type) {
        nodes.putIfAbsent(id, new PageNode(id, name, type));
        adjacencyList.putIfAbsent(id, new ArrayList<>());
    }

    
    public void addElementToPage(String pageId, Element element) {
        PageNode pageNode = nodes.get(pageId);
        if (pageNode == null) {
            addPage(pageId, "stack");
            pageNode = nodes.get(pageId);
        }
        pageNode.addElement(element);
        
        // 如果元素指向另一个页面，则建立页面间的关系
        if (element.getTargetPageId() != null) {
            // 确保目标页面存在
            if (!nodes.containsKey(element.getTargetPageId())) {
                addPage(element.getTargetPageId(), "stack");
            }
            // 添加到邻接表中，确保不重复添加
            Edge edge = new Edge(element.getTargetPageId(), element.getId(), element.getLabel());
            if (!adjacencyList.get(pageId).contains(edge)) {
                adjacencyList.get(pageId).add(edge);
            }
        }
    }
    
    /**
     * 同步所有页面元素的targetPageId与邻接表关系
     * 确保邻接表中包含了所有元素指向的关系
     */
    public void syncAdjacencyList() {
        // 清空现有的邻接表关系
        for (List<Edge> neighbors : adjacencyList.values()) {
            neighbors.clear();
        }
        
        // 重新根据元素的targetPageId建立关系
        for (Map.Entry<String, PageNode> entry : nodes.entrySet()) {
            String pageId = entry.getKey();
            PageNode pageNode = entry.getValue();
            
            if (pageNode.elements != null) {
                for (Element element : pageNode.elements) {
                    String targetPageId = element.getTargetPageId();
                    if (targetPageId != null) {
                        // 确保目标页面存在
                        if (!nodes.containsKey(targetPageId)) {
                            addPage(targetPageId, "stack");
                        }
                        // 添加到邻接表中，确保不重复添加
                        Edge edge = new Edge(targetPageId, element.getId(), element.getLabel());
                        if (!adjacencyList.get(pageId).contains(edge)) {
                            adjacencyList.get(pageId).add(edge);
                        }
                    }
                }
            }
        }
    }
    
    /**
     * 获取所有页面ID的列表
     * @return 页面ID列表
     */
    public List<String> listPages() {
        return new ArrayList<>(nodes.keySet());
    }
    
    /**
     * 获取指定页面的JSON表示
     * @param pageId 页面ID
     * @return 页面的JSON字符串
     */
    public String getPage(String pageId) {
        PageNode pageNode = nodes.get(pageId);
        if (pageNode == null) {
            return null;
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("{")
          .append("\"id\":\"").append(pageNode.getId()).append("\",");
          
        if (pageNode.getName() != null) {
            sb.append("\"name\":\"").append(pageNode.getName()).append("\",");
        }
          
        sb.append("\"type\":\"").append(pageNode.getType()).append("\"");
          
        // 添加元素信息
        if (pageNode.elements != null && !pageNode.elements.isEmpty()) {
            sb.append(",\"elements\":[");
            for (int i = 0; i < pageNode.elements.size(); i++) {
                if (i > 0) sb.append(",");
                Element element = pageNode.elements.get(i);
                sb.append("{");
                
                if (element.getId() != null) {
                    sb.append("\"id\":\"").append(element.getId()).append("\",");
                }
                
                sb.append("\"type\":\"").append(element.getType() != null ? element.getType() : "").append("\",")
                  .append("\"text\":\"").append(element.getText() != null ? element.getText() : "").append("\",")
                  .append("\"label\":\"").append(element.getLabel() != null ? element.getLabel() : "").append("\",")
                  .append("\"x\":").append(element.getX()).append(",")
                  .append("\"y\":").append(element.getY()).append(",")
                  .append("\"width\":").append(element.getWidth()).append(",")
                  .append("\"height\":").append(element.getHeight());
                  
                // 添加指向页面信息
                if (element.getTargetPageId() != null) {
                    sb.append(",\"targetPageId\":\"").append(element.getTargetPageId()).append("\"");
                }
                  
                sb.append("}");
            }
            sb.append("]");
        }
        sb.append("}");
        
        return sb.toString();
    }

    // BFS 查找路径
    public List<String> findPath(String start, String end) {
        Queue<List<String>> queue = new LinkedList<>();
        Set<String> visited = new HashSet<>();

        queue.add(Collections.singletonList(start));

        while (!queue.isEmpty()) {
            List<String> path = queue.poll();
            String node = path.get(path.size() - 1);

            if (node.equals(end)) {
                return path;
            }

            visited.add(node);

            for (Edge edge : adjacencyList.getOrDefault(node, new ArrayList<>())) {
                String neighbor = edge.getTargetPageId();
                if (!visited.contains(neighbor)) {
                    List<String> newPath = new ArrayList<>(path);
                    newPath.add(neighbor);
                    queue.add(newPath);
                }
            }
        }
        return null;
    }
    
    /**
     * 查找路径并包含元素信息
     * @param start 起始页面ID
     * @param end 结束页面ID
     * @return 包含元素信息的路径，格式为 page(element) -> page
     */
    public List<String> findPathWithElements(String start, String end) {
        Queue<List<PathNode>> queue = new LinkedList<>();
        Set<String> visited = new HashSet<>();
        
        // 初始化路径，起始节点没有前置元素
        List<PathNode> initialPath = new ArrayList<>();
        initialPath.add(new PathNode(start, null));
        queue.add(initialPath);
        
        while (!queue.isEmpty()) {
            List<PathNode> path = queue.poll();
            PathNode currentNode = path.get(path.size() - 1);
            String currentNodeId = currentNode.getPageId();
            
            if (currentNodeId.equals(end)) {
                // 找到路径，转换为字符串列表
                List<String> result = new ArrayList<>();
                for (int i = 0; i < path.size(); i++) {
                    PathNode node = path.get(i);
                    if (node.getElementId() != null) {
                        result.add(node.getPageId() + "(" + node.getElementId() + ")");
                    } else {
                        result.add(node.getPageId());
                    }
                }
                return result;
            }
            
            visited.add(currentNodeId);
            
            // 获取当前页面的所有元素
            PageNode currentPageNode = nodes.get(currentNodeId);
            if (currentPageNode != null && currentPageNode.elements != null) {
                for (Element element : currentPageNode.elements) {
                    String targetPageId = element.getTargetPageId();
                    if (targetPageId != null && !visited.contains(targetPageId)) {
                        // 创建新路径
                        List<PathNode> newPath = new ArrayList<>(path);
                        newPath.add(new PathNode(targetPageId, element.getId()));
                        queue.add(newPath);
                    }
                }
            }
        }
        return null;
    }

    // 遍历所有节点，给出从指定起始节点出发的所有路径
    public List<List<String>> findAllPathsFrom(String start) {
        List<List<String>> allPaths = new ArrayList<>();
        Set<String> visited = new HashSet<>();
        List<String> currentPath = new ArrayList<>();
        
        dfsAllPaths(start, visited, currentPath, allPaths);
        
        return allPaths;
    }
    
    // DFS遍历所有路径的辅助方法
    private void dfsAllPaths(String node, Set<String> visited, List<String> currentPath, List<List<String>> allPaths) {
        visited.add(node);
        currentPath.add(node);
        
        // 添加当前路径到结果中（如果路径长度大于1，即包含起始节点和至少一个节点）
        if (currentPath.size() > 1) {
            allPaths.add(new ArrayList<>(currentPath));
        }
        
        // 递归遍历所有邻居节点
        for (Edge edge : adjacencyList.getOrDefault(node, new ArrayList<>())) {
            String neighbor = edge.getTargetPageId();
            if (!visited.contains(neighbor)) {
                dfsAllPaths(neighbor, visited, currentPath, allPaths);
            }
        }
        
        // 回溯
        visited.remove(node);
        currentPath.remove(currentPath.size() - 1);
    }

    // 遍历所有节点，给出图中所有可能的路径
    public List<List<String>> findAllPaths() {
        List<List<String>> allPaths = new ArrayList<>();
        
        // 对每个节点作为起始点进行遍历
        for (String node : nodes.keySet()) {
            List<List<String>> pathsFromNode = findAllPathsFrom(node);
            allPaths.addAll(pathsFromNode);
        }
        
        return allPaths;
    }

    // 环路检测
    public boolean hasCycle() {
        Set<String> visited = new HashSet<>();
        Set<String> stack = new HashSet<>();

        for (String node : nodes.keySet()) {
            if (dfsCycle(node, visited, stack)) {
                return true;
            }
        }
        return false;
    }

    private boolean dfsCycle(String node, Set<String> visited, Set<String> stack) {
        if (stack.contains(node)) return true;
        if (visited.contains(node)) return false;

        visited.add(node);
        stack.add(node);

        for (Edge edge : adjacencyList.getOrDefault(node, new ArrayList<>())) {
            String neighbor = edge.getTargetPageId();
            if (dfsCycle(neighbor, visited, stack)) {
                return true;
            }
        }

        stack.remove(node);
        return false;
    }

    // 导出 JSON（跨平台共享）
    public String toJson() {
        StringBuilder sb = new StringBuilder();
        sb.append("{ \"nodes\": [");
        boolean first = true;
        for (PageNode node : nodes.values()) {
            if (!first) sb.append(",");
            sb.append("{\"id\":\"").append(node.getId()).append("\",");
            
            if (node.getName() != null) {
                sb.append("\"name\":\"").append(node.getName()).append("\",");
            }
            
            sb.append("\"type\":\"").append(node.getType()).append("\"");
              
            // 添加元素信息
            if (node.elements != null && !node.elements.isEmpty()) {
                sb.append(",\"elements\":[");
                for (int i = 0; i < node.elements.size(); i++) {
                    if (i > 0) sb.append(",");
                    Element element = node.elements.get(i);
                    sb.append("{");
                    
                    if (element.getId() != null) {
                        sb.append("\"id\":\"").append(element.getId()).append("\",");
                    }
                    
                    sb.append("\"type\":\"").append(element.getType() != null ? element.getType() : "").append("\",")
                      .append("\"text\":\"").append(element.getText() != null ? element.getText() : "").append("\",")
                      .append("\"label\":\"").append(element.getLabel() != null ? element.getLabel() : "").append("\",")
                      .append("\"x\":").append(element.getX()).append(",")
                      .append("\"y\":").append(element.getY()).append(",")
                      .append("\"width\":").append(element.getWidth()).append(",")
                      .append("\"height\":").append(element.getHeight());
                      
                    // 添加指向页面信息
                    if (element.getTargetPageId() != null) {
                        sb.append(",\"targetPageId\":\"").append(element.getTargetPageId()).append("\"");
                    }
                      
                    sb.append("}");
                }
                sb.append("]");
            }
            sb.append("}");
            first = false;
        }
        sb.append("], \"edges\": [");
        first = true;
        for (Map.Entry<String, List<Edge>> entry : adjacencyList.entrySet()) {
            for (Edge edge : entry.getValue()) {
                if (!first) sb.append(",");
                sb.append("{\"from\":\"").append(entry.getKey())
                        .append("\",\"to\":\"").append(edge.getTargetPageId())
                        .append("\",\"elementId\":\"").append(edge.getElementId() != null ? edge.getElementId() : "")
                        .append("\",\"elementName\":\"").append(edge.getElementName() != null ? edge.getElementName() : "")
                        .append("\"}");
                first = false;
            }
        }
        sb.append("]}");
        return sb.toString();
    }
}