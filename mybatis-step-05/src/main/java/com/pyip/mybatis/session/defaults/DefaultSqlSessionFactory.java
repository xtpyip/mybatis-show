package com.pyip.mybatis.session.defaults;

import com.pyip.mybatis.session.Configuration;
import com.pyip.mybatis.session.SqlSession;
import com.pyip.mybatis.session.SqlSessionFactory;

public class DefaultSqlSessionFactory implements SqlSessionFactory {

    private final Configuration configuration;

    public DefaultSqlSessionFactory(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public SqlSession openSession() {
        return new DefaultSqlSession(configuration);
    }
}
