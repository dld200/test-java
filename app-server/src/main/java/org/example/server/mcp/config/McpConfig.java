package org.example.server.mcp.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.modelcontextprotocol.server.transport.WebMvcStreamableServerTransportProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

@Configuration
@EnableWebMvc
class McpConfig {
    @Bean
    WebMvcStreamableServerTransportProvider webMvcStreamableHttpServerTransportProvider(ObjectMapper mapper) {
        return WebMvcStreamableServerTransportProvider.builder().objectMapper(mapper).mcpEndpoint("/mcp/message").build();
    }

    @Bean
    RouterFunction<ServerResponse> mcpRouterFunction(WebMvcStreamableServerTransportProvider transportProvider) {
        return transportProvider.getRouterFunction();
    }
}