package com.pyip.mybatis.binding;

import cn.hutool.core.lang.ClassScanner;
import com.pyip.mybatis.session.SqlSession;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MapperRegistry {
    // 将已添加的映射器代理加入到HashMap缓存中
    private final Map<Class<?>,MapperProxyFactory<?>> knowMappers = new HashMap<>();

    // 通过Class类和sqlSession获取代理类
    public <T> T getMapper(Class<T> type, SqlSession sqlSession){
        final MapperProxyFactory<T> mapperProxyFactory = (MapperProxyFactory<T>) knowMappers.get(type);
        if(mapperProxyFactory == null){
            throw new RuntimeException("Type "+type+" is not know to the MapperRegistry.");
        }
        try{
            return mapperProxyFactory.newInstance(sqlSession);
        }catch (Exception e){
            throw new RuntimeException("Error getting mapper instance.Cause: "+e,e);
        }
    }

    public <T> void addMapper(Class<T> type){
        // Mapper必须是接口才能注册 IUserDao,ISchoolDao
        if(type.isInterface()){
            if(hasMapper(type)){
                // 重复添加
                throw new RuntimeException("Type "+ type +" is already know to the MapperRegistry.");
            }
            // 注册映射器代理工厂
            knowMappers.put(type,new MapperProxyFactory<>(type));
        }
    }

    // 返回是否存在此Class
    public <T> boolean hasMapper(Class<T> type) {
        return knowMappers.containsKey(type);
    }

    // 对指定包下的所有接口进行注册
    public void addMappers(String packageName){
        Set<Class<?>> mapperSet = ClassScanner.scanPackage(packageName);
        for (Class<?> mapperClass : mapperSet) {
            addMapper(mapperClass);
        }
    }
}
