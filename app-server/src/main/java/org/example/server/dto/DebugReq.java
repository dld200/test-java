package org.example.server.dto;

import lombok.Data;

import java.util.Map;

@Data
public class DebugReq {

    private Map<String, Object> variables;

    private String script;
}
