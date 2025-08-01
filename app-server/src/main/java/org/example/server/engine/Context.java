package org.example.server.engine;

import lombok.Data;
import org.example.common.domain.Device;
import org.example.common.domain.TestCase;

import java.util.HashMap;
import java.util.Map;

@Data
public class Context {
    private TestCase testCase;
    private Map<String, Object> options;
    private Map<String, Object> variables;
    private Object result;
    private Device device;

    public Context(TestCase testCase) {
        this.testCase = testCase;
        this.options = new HashMap<>();
        this.variables = new HashMap<>();
    }

    public void setVariable(String name, Object value) {
        this.variables.put(name, value);
    }

    public Object getVariable(String name) {
        return this.variables.get(name);
    }

    public void setOption(String name, Object value) {
        this.options.put(name, value);
    }

    public Object getOption(String name) {
        return this.options.get(name);
    }
}