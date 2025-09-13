package org.example.server.engine.step;

import lombok.Data;
import org.example.common.domain.TestStep;
import org.example.server.engine.ExecuteContext;
import org.example.server.engine.StepFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Data
public class HttpStep implements IStep {
    private String method;
    private String url;
    private Map<String, String> headers;
    private String body;


    // 静态代码块自动注册
    static {
        StepFactory.registerStep("http", SqlStep.class);
    }

    @Override
    public String getType() {
        return "http";
    }

    @Override
    public void execute(TestStep testStep, ExecuteContext context) {

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

        String result = response.getBody();
        context.addStepResult(testStep.getId(), result);
    }
}