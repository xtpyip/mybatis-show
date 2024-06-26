package com.pyip.mybatis;

/**
 * 生产sql session会话
 */
public interface SqlSessionFactory {
    // 新建一个会话
    SqlSession openSession();
}
