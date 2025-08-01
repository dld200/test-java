package org.example.server.dao;

import org.apache.ibatis.annotations.Mapper;
import org.example.common.domain.Transaction;
import org.example.server.config.BaseDao;

@Mapper
public interface TransactionDao extends BaseDao<Transaction> {
    // 可以在这里添加Transaction特有的数据库操作方法
}