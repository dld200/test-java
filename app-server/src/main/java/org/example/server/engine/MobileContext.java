package org.example.server.engine;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.common.domain.TestDevice;
import org.example.common.domain.TestCase;
import org.example.mobile.automation.Automation;
import org.example.mobile.dto.Device;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MobileContext {
    private TestCase testCase;
    private Map<String, Object> options;
    private Map<String, Object> variables;
    private String result;
    private Device device;
    private String deviceId;
    private String bundleId;
    private Automation automation;

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