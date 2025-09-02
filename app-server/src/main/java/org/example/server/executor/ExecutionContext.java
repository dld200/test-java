package org.example.server.executor;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class ExecutionContext {
    private Map<String, String> variables = new HashMap<>();

    public void setVariable(String key, String value) {
        variables.put(key, value);
    }

    public String getVariable(String key) {
        return variables.get(key);
    }

    public boolean containsVariable(String key) {
        return variables.containsKey(key);
    }

    public String replaceVariables(String text) {
        if (text == null) {
            return null;
        }

        String result = text;
        for (Map.Entry<String, String> entry : variables.entrySet()) {
            String placeholder = "${" + entry.getKey() + "}";
            result = result.replace(placeholder, entry.getValue());
        }
        return result;
    }

    public void addStepResult(Long id, String result) {

    }
}