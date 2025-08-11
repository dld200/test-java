package org.example.server.service;

import lombok.extern.slf4j.Slf4j;
import org.example.server.dao.TransactionDao;
import org.example.common.domain.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class TransactionService {

    @Autowired
    private TransactionDao transactionDao;

    public void save(Transaction transaction) {
        transaction.setCreateTime(new Date());
        transaction.setUpdateTime(new Date());
        transactionDao.save(transaction);
    }

    public Transaction find(String url, String request) {
        // 使用JPA的方式进行查询
        List<Transaction> transactions = transactionDao.findByUrlAndRequest(url, request);
        return transactions.isEmpty() ? null : transactions.get(0);
    }

    public List<Transaction> getAllTransactions() {
        return transactionDao.findAll();
    }

    public void clear() {
        // 注意：在生产环境中，不应删除所有记录
        transactionDao.deleteAll(); // 删除所有记录
    }
}