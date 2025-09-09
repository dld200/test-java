package org.example.server.dto;

import lombok.Data;
import org.example.common.domain.PageModel;

import java.util.Map;

@Data
public class ModelReq {

    private Map<String, Object> variables;

    private String script;
    
    private String name;
    
    private String xml;
    
    private String json;
    
    private String html;
    
    private String screenshot;
    
    private String summary;
}