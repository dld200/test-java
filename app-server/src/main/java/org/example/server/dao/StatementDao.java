package org.example.server.dao;

import org.example.common.domain.Statement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StatementDao extends JpaRepository<Statement, Long> {
    // 可以在这里添加Statement特有的数据库操作方法
}