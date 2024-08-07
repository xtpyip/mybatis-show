package com.pyip.mybatis.executor;

import com.pyip.mybatis.cache.CacheKey;
import com.pyip.mybatis.mapping.BoundSql;
import com.pyip.mybatis.mapping.MappedStatement;
import com.pyip.mybatis.session.ResultHandler;
import com.pyip.mybatis.session.RowBounds;
import com.pyip.mybatis.transaction.Transaction;

import java.sql.SQLException;
import java.util.List;

/**
 * @author 小傅哥，微信：fustack
 * @description 执行器
 * @github https://github.com/fuzhengwei
 * @copyright 公众号：bugstack虫洞栈 | 博客：https://bugstack.cn - 沉淀、分享、成长，让自己和他人都能有所收获！
 */
public interface Executor {

    ResultHandler NO_RESULT_HANDLER = null;

    int update(MappedStatement ms, Object parameter) throws SQLException;

    // 查询，含缓存
    <E> List<E> query(MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, CacheKey key, BoundSql boundSql) throws SQLException;

    // 查询
    <E> List<E> query(MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler) throws SQLException;

    Transaction getTransaction();

    void commit(boolean required) throws SQLException;

    void rollback(boolean required) throws SQLException;

    void close(boolean forceRollback);

    // 清理Session缓存
    void clearLocalCache();

    // 创建缓存 Key
    CacheKey createCacheKey(MappedStatement ms, Object parameterObject, RowBounds rowBounds, BoundSql boundSql);

    void setExecutorWrapper(Executor executor);

}