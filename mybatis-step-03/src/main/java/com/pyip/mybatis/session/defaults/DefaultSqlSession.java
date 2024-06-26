package com.pyip.mybatis.session.defaults;

import com.pyip.mybatis.binding.MapperRegistry;
import com.pyip.mybatis.session.SqlSession;

public class DefaultSqlSession implements SqlSession {
    private MapperRegistry mapperRegistry;

    public DefaultSqlSession(MapperRegistry mapperRegistry){
        this.mapperRegistry = mapperRegistry;
    }
    @Override
    public <T> T selectOne(String statement) {
        return (T) ("你的操作"+statement+"被代理了");
    }

    @Override
    public <T> T selectOne(String statement, Object parameter) {
        return (T) ("你的操作被代理了！" + "方法：" + statement + " 入参：" + parameter);
    }

    @Override
    public <T> T getMapper(Class<T> type) {
        return mapperRegistry.getMapper(type, this);
    }
}
