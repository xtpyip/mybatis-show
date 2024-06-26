package com.pyip.mybatis.session;

import com.pyip.mybatis.binding.MapperRegistry;
import com.pyip.mybatis.mapping.MappedStatement;

import java.util.Map;
import java.util.HashMap;

public class Configuration {
    //映射注册机
    protected MapperRegistry mapperRegistry = new MapperRegistry(this);

    // 映射的语句，存在Map里
    protected final Map<String, MappedStatement> mappedStatements = new HashMap<>();

    // 添加包下的所有接口
    public void addMappers(String packageName) {
        mapperRegistry.addMappers(packageName);
    }
    // 为type类添加接口
    public <T> void addMapper(Class<T> type) {
        mapperRegistry.addMapper(type);
    }
    // 获取代理类对象（映射器）
    public <T> T getMapper(Class<T> type, SqlSession sqlSession) {
        return mapperRegistry.getMapper(type, sqlSession);
    }
    // 判断是否存在此类
    public boolean hasMapper(Class<?> type) {
        return mapperRegistry.hasMapper(type);
    }
    // 为mappedStatement 添加mappedStatement
    public void addMappedStatement(MappedStatement ms) {
        mappedStatements.put(ms.getId(), ms);
    }
    // 通过key值 id来查找对应的mappedStatement
    public MappedStatement getMappedStatement(String id) {
        return mappedStatements.get(id);
    }
}
