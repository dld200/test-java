package org.example.server.dao;

import org.apache.ibatis.annotations.Mapper;
import org.example.common.domain.Statement;
import org.example.server.config.BaseDao;

@Mapper
public interface StatementDao extends BaseDao<Statement> {
    // 可以在这里添加Statement特有的数据库操作方法
}