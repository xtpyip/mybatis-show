package com.pyip.mybatis.session.defaults;

import com.pyip.mybatis.binding.MapperRegistry;
import com.pyip.mybatis.session.SqlSession;
import com.pyip.mybatis.session.SqlSessionFactory;

public class DefaultSqlSessionFactory implements SqlSessionFactory {
    private final MapperRegistry mapperRegistry;
    public DefaultSqlSessionFactory(MapperRegistry mapperRegistry){
        this.mapperRegistry = mapperRegistry;
    }
    @Override
    public SqlSession openSession() {
        return new DefaultSqlSession(mapperRegistry);
    }
}
