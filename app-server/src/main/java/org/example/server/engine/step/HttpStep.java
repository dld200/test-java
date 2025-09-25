package org.example.server.engine.step;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import lombok.Data;
import org.example.common.domain.TestStep;
import org.example.server.engine.ExecuteContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
@Component
public class HttpStep implements IStep {

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public String getType() {
        return "http";
    }

    @Override
    public String execute(TestStep testStep, String params, ExecuteContext context) {
        String method = JSON.parseObject(params).getString("method");
        String url = JSON.parseObject(params).getString("url");
        List<Map<String, Object>> keyValues = JSON.parseObject(params).getObject("headers", new TypeReference<>() {
        });
        Map<String, Object> headers = keyValues.stream()
                .collect(Collectors.toMap(m -> m.get("key").toString(),
                        m -> m.get("value")));

        String body = JSON.parseObject(params).getString("body");

        // 创建HTTP头
        HttpHeaders httpHeaders = new HttpHeaders();
        for (Map.Entry<String, Object> entry : headers.entrySet()) {
            httpHeaders.add(entry.getKey(), entry.getValue().toString());
        }

        HttpEntity<String> entity = new HttpEntity<>(body, httpHeaders);

        HttpMethod httpMethod;
        try {
            httpMethod = HttpMethod.valueOf(method.toUpperCase());
        } catch (IllegalArgumentException e) {
            httpMethod = HttpMethod.GET;
        }

        ResponseEntity<String> response = restTemplate.exchange(
                url,
                httpMethod,
                entity,
                String.class
        );

        return response.getBody();
    }
}