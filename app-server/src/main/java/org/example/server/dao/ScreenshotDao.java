package org.example.server.dao;


import org.example.common.domain.Screenshot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScreenshotDao extends JpaRepository<Screenshot, Long> {
    // 可以在这里添加Screenshot特有的数据库操作方法
}