package org.example.server.dto;

import lombok.Data;

import java.util.Map;

@Data
public class ModelReq {

    private Map<String, Object> variables;

    private String script;
}
