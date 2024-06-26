package com.pyip.mybatis.session.defaults;

import com.pyip.mybatis.mapping.MappedStatement;
import com.pyip.mybatis.session.Configuration;
import com.pyip.mybatis.session.SqlSession;

public class DefaultSqlSession implements SqlSession {
    private Configuration configuration;

    public DefaultSqlSession(Configuration configuration) {
        this.configuration = configuration;
    }
    @Override
    public <T> T selectOne(String statement) {
        return (T) ("你的操作被代理了！" + statement);
    }

    @Override
    public <T> T selectOne(String statement, Object parameter) {
        MappedStatement mappedStatement = configuration.getMappedStatement(statement);
        return (T) ("你的操作被代理了！" + "\n方法：" + statement + "\n入参："
                + parameter + "\n待执行SQL：" + mappedStatement.getSql());
    }
    @Override
    public Configuration getConfiguration() {
        return configuration;
    }

    @Override
    public <T> T getMapper(Class<T> type) {
        return configuration.getMapper(type, this);
    }
}
