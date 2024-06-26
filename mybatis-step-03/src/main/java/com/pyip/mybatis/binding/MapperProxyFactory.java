package com.pyip.mybatis.binding;

import com.pyip.mybatis.session.SqlSession;

import java.lang.reflect.Proxy;

public class MapperProxyFactory<T> {
    private final Class<T> mapperInterface;
    public MapperProxyFactory(Class<T> mapperInterface){
        this.mapperInterface = mapperInterface;
    }

    @SuppressWarnings("unchecked")
    public T newInstance(SqlSession sqlSession) {
        final MapperProxy<T> mapperProxy = new MapperProxy<>(mapperInterface,sqlSession);
        return (T) Proxy.newProxyInstance(mapperInterface.getClassLoader(),
                new Class[]{mapperInterface},mapperProxy);
    }

}
