package com.pyip.mybatis;

import java.util.List;

/**
 * 对数据库进行定义和处理的类
 */
public interface SqlSession {
    // 无参查询单个
    <T> T selectOne(String statement);
    // 按条件查询单个
    <T> T selectOne(String statement,Object parameter);
    // 无参查询多个
    <T> List<T> selectList(String statement);
    // 按条件查询多个
    <T> List<T> selectList(String statement, Object parameter);
    void close();
}
