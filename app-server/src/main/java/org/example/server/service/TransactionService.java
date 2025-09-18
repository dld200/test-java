package org.example.server.service;

import lombok.extern.slf4j.Slf4j;
import org.example.server.dao.TransactionDao;
import org.example.common.domain.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class TransactionService {

    @Autowired
    private TransactionDao transactionDao;

    public void save(Record record) {
        record.setCreateTime(new Date());
        record.setUpdateTime(new Date());
        transactionDao.save(record);
    }

    public Record find(String url, String request) {
        // 使用JPA的方式进行查询
        List<Record> records = transactionDao.findByUrlAndRequest(url, request);
        return records.isEmpty() ? null : records.get(0);
    }

    public List<Record> getAllTransactions() {
        return transactionDao.findAll();
    }

    public void clear() {
        // 注意：在生产环境中，不应删除所有记录
        transactionDao.deleteAll(); // 删除所有记录
    }
}