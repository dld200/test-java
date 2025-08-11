package org.example.server.dao;

import org.example.common.domain.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionDao extends JpaRepository<Transaction, Long> {
    // 可以在这里添加Transaction特有的数据库操作方法
    List<Transaction> findByUrlAndRequest(String url, String request);
}