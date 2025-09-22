package org.example.server.engine;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class ExecuteContext {

    private MobileContext mobileContext;

//    private Map<Long, String> stepResults = new HashMap<>();

    private Map<String, Object> runtimeVariables = new HashMap<String, Object>();

    public String resolve(String expression) {
        if (expression == null) {
            return null;
        }
        String result = expression;
        for (Map.Entry<String, Object> entry : runtimeVariables.entrySet()) {
            String placeholder = "${" + entry.getKey() + "}";
            //todo: string要加引号？用户自己加
            result = result.replace(placeholder, entry.getValue().toString());
        }
        return result;
    }

    //保存结果，给后续引用
    public void setStepResult(Long id, String result) {
//        stepResults.put(id, result);
        //如果是json，解析出来放入runtimeVariables
        if (result != null && result.startsWith("{")) {
            Map<String, Object> map = JSON.parseObject(result, new TypeReference<>() {
            });
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                if (runtimeVariables.containsKey(entry.getKey())) {
                    //直接覆盖
                    runtimeVariables.put(entry.getKey(), entry.getValue());
                } else {
                    runtimeVariables.put("Step" + id + "." + entry.getKey(), entry.getValue());
                }
            }
        } else {
            runtimeVariables.put("Step" + id + ".output", result);
        }
    }
}