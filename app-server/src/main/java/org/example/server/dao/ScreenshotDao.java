package org.example.server.dao;

import org.apache.ibatis.annotations.Mapper;
import org.example.common.domain.Screenshot;
import org.example.server.config.BaseDao;

@Mapper
public interface ScreenshotDao extends BaseDao<Screenshot> {
    // 可以在这里添加Screenshot特有的数据库操作方法
}