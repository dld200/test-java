package org.example.server.service;

import groovy.util.logging.Slf4j;
import org.example.server.dao.TransactionDao;
import org.example.common.domain.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

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
        transactionDao.insert(transaction);
    }

    public Transaction find(String url, String request) {
        // 使用Tk Mapper的Example进行查询
        Example example = new Example(Transaction.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("url", url);
        criteria.andEqualTo("request", request);

        List<Transaction> transactions = transactionDao.selectByExample(example);
        return transactions.isEmpty() ? null : transactions.get(0);
    }

    public List<Transaction> getAllTransactions() {
        return transactionDao.selectAll();
    }

    public void clear() {
        // 注意：在生产环境中，不应删除所有记录
        transactionDao.deleteByExample(null); // 删除所有记录
    }
}
