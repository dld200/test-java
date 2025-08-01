package org.example.server.config;

import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

/**
 * 通用DAO接口，继承Tk Mapper的通用接口
 * @param <T> 实体类类型
 */
public interface BaseDao<T> extends Mapper<T>, MySqlMapper<T> {
    // 可以在这里添加通用的自定义方法
}