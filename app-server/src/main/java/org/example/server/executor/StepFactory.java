package org.example.server.executor;

import com.alibaba.fastjson.JSON;

import java.util.HashMap;
import java.util.Map;

public class StepFactory {
    private static final Map<String, Class<? extends IStep>> registry = new HashMap<>();

    // 注册方法
    public static void registerStep(String type, Class<? extends IStep> clazz) {
        registry.put(type.toLowerCase(), clazz);
    }

    // 创建实例
    public static IStep createStep(String type, String configJson, ExecutionContext context) {
        Class<? extends IStep> clazz = registry.get(type.toLowerCase());
        if (clazz == null) {
            throw new IllegalArgumentException("Unknown step type: " + type);
        }
        // 反序列化配置，生成具体 Step
        return JSON.parseObject(context.replaceVariables(configJson), clazz);
    }
}
