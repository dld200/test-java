package org.example.server.engine.step;


import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class HttpStepConfig {

    @Bean
    public RestTemplate restTemplate() {
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(200);             // 最大连接数为 200
        connectionManager.setDefaultMaxPerRoute(20);    // 每个路由的最大连接数为 20

        CloseableHttpClient httpClient = HttpClients.custom()   // 建 HTTP 客户端
                .setConnectionManager(connectionManager)
                .build();

        // 创建请求工
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient);

        // 返回 RestTemplate
        return new RestTemplate(factory);
    }
}