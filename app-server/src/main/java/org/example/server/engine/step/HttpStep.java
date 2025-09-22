package org.example.server.engine.step;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import lombok.Data;
import org.example.common.domain.TestStep;
import org.example.server.engine.ExecuteContext;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Data
@Component
public class HttpStep implements IStep {
    @Override
    public String getType() {
        return "http";
    }

    @Override
    public String execute(TestStep testStep, String params, ExecuteContext context) {
        String method = JSON.parseObject(params).getString("method");
        String url = JSON.parseObject(params).getString("url");
        Map<String, String> headers = JSON.parseObject(params).getObject("headers", new TypeReference<>() {
        });
        String body = JSON.parseObject(params).getString("body");

        RestTemplate restTemplate = new RestTemplate();

        // 创建HTTP头
        HttpHeaders httpHeaders = new HttpHeaders();
        if (headers != null) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                httpHeaders.add(entry.getKey(), entry.getValue());
            }
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