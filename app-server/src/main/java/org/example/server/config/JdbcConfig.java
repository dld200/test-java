package org.example.server.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class JdbcConfig {

    @Bean
    @ConfigurationProperties("spring.datasource.stage")
    public DataSource stageDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean
    public JdbcTemplate stageJdbcTemplate(@Qualifier("db1DataSource") DataSource ds) {
        return new JdbcTemplate(ds);
    }

}