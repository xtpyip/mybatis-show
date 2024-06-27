package com.pyip.mybatis.session;

import com.pyip.mybatis.binding.MapperRegistry;
import com.pyip.mybatis.datasource.druid.DruidDataSourceFactory;
import com.pyip.mybatis.datasource.pooled.PooledDataSourceFactory;
import com.pyip.mybatis.datasource.unpooled.UnpooledDataSourceFactory;
import com.pyip.mybatis.executor.Executor;
import com.pyip.mybatis.executor.SimpleExecutor;
import com.pyip.mybatis.executor.resultset.DefaultResultSetHandler;
import com.pyip.mybatis.executor.resultset.ResultSetHandler;
import com.pyip.mybatis.executor.statement.PreparedStatementHandler;
import com.pyip.mybatis.executor.statement.StatementHandler;
import com.pyip.mybatis.mapping.BoundSql;
import com.pyip.mybatis.mapping.Environment;
import com.pyip.mybatis.mapping.MappedStatement;
import com.pyip.mybatis.transaction.Transaction;
import com.pyip.mybatis.transaction.jdbc.JdbcTransactionFactory;
import com.pyip.mybatis.type.TypeAliasRegistry;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 小傅哥，微信：fustack
 * @description 配置项
 * @github https://github.com/fuzhengwei
 * @copyright 公众号：bugstack虫洞栈 | 博客：https://bugstack.cn - 沉淀、分享、成长，让自己和他人都能有所收获！
 */
public class Configuration {

    //环境
    protected Environment environment;

    // 映射注册机
    protected MapperRegistry mapperRegistry = new MapperRegistry(this);

    // 映射的语句，存在Map里
    protected final Map<String, MappedStatement> mappedStatements = new HashMap<>();

    // 类型别名注册机
    protected final TypeAliasRegistry typeAliasRegistry = new TypeAliasRegistry();

    public Configuration() {
        typeAliasRegistry.registerAlias("JDBC", JdbcTransactionFactory.class);

        typeAliasRegistry.registerAlias("DRUID", DruidDataSourceFactory.class);
        typeAliasRegistry.registerAlias("UNPOOLED", UnpooledDataSourceFactory.class);
        typeAliasRegistry.registerAlias("POOLED", PooledDataSourceFactory.class);
    }

    public void addMappers(String packageName) {
        mapperRegistry.addMappers(packageName);
    }

    public <T> void addMapper(Class<T> type) {
        mapperRegistry.addMapper(type);
    }

    public <T> T getMapper(Class<T> type, SqlSession sqlSession) {
        return mapperRegistry.getMapper(type, sqlSession);
    }

    public boolean hasMapper(Class<?> type) {
        return mapperRegistry.hasMapper(type);
    }

    public void addMappedStatement(MappedStatement ms) {
        mappedStatements.put(ms.getId(), ms);
    }

    public MappedStatement getMappedStatement(String id) {
        return mappedStatements.get(id);
    }

    public TypeAliasRegistry getTypeAliasRegistry() {
        return typeAliasRegistry;
    }

    public Environment getEnvironment() {
        return environment;
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    /**
     * 创建结果集处理器
     */
    public ResultSetHandler newResultSetHandler(Executor executor, MappedStatement mappedStatement, BoundSql boundSql) {
        return new DefaultResultSetHandler(executor, mappedStatement, boundSql);
    }

    /**
     * 生产执行器
     */
    public Executor newExecutor(Transaction transaction) {
        return new SimpleExecutor(this, transaction);
    }

    /**
     * 创建语句处理器
     */
    public StatementHandler newStatementHandler(Executor executor, MappedStatement mappedStatement, Object parameter, ResultHandler resultHandler, BoundSql boundSql) {
        return new PreparedStatementHandler(executor, mappedStatement, parameter, resultHandler, boundSql);
    }

}
