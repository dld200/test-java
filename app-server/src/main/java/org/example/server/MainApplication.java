package org.example.server;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@Slf4j
@EnableScheduling
@SpringBootApplication
//@EntityScan(basePackages = "org.example.common.domain")
//@EnableJpaRepositories(basePackages = "org.example.server.dao")
public class MainApplication {

    public static void main(String[] args) {
        SpringApplication.run(MainApplication.class, args);
    }

//    @Tool(description = "Get weather information by city name")
//    public String getWeather(String cityName) {
//        // Implementation
//        return "Weather information for " + cityName;
//    }
//
//    @Bean
//    public ToolCallbackProvider weatherTools(WeatherService weatherService) {
//        return MethodToolCallbackProvider.builder().toolObjects(weatherService).build();
//    }

}