package com.pyip.mybatis.binding;

import java.lang.reflect.Proxy;
import java.util.Map;

public class MapperProxyFactory<T> {
    private final Class<T> mapperInterface;
    public MapperProxyFactory(Class<T> mapperInterface){
        // 传入的mapperInterface是接口的.class 如IUserDao.class com.pyip.mybatis.test.dao.IUserDao
        this.mapperInterface = mapperInterface;
    }

    /**
     * key: com.pyip.mybatis.test.dao.IUserDao.queryUserName 全限定类名+'.'+方法名
     */
    public T newInstance(Map<String,String> sqlSession){
        final MapperProxy<T> mapperProxy = new MapperProxy<>(sqlSession,mapperInterface);
        return (T) Proxy.newProxyInstance(mapperInterface.getClassLoader(),
                new Class[]{mapperInterface},mapperProxy);
    }
}
