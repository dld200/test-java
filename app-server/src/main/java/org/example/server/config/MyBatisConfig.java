package org.example.server.config;

import org.springframework.context.annotation.Configuration;
import tk.mybatis.spring.annotation.MapperScan;

@Configuration
@MapperScan(basePackages = "org.example.server.dao", markerInterface = BaseDao.class)
public class MyBatisConfig {
    // MyBatis配置类，用于扫描DAO接口
}