package com.pyip.mybatis.binding;

import com.pyip.mybatis.session.SqlSession;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class MapperProxy<T> implements InvocationHandler, Serializable {
    private static final long serialVersionUID = -6424540398559729838L;
    private Class<T> mapperInterface;
    private final SqlSession sqlSession;
    public MapperProxy(Class<T> type,SqlSession sqlsession){
        this.mapperInterface = type;
        this.sqlSession = sqlsession;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if(Object.class.equals(method.getDeclaringClass())){
            // 调用toString,equals,hashCode等父类方法
            return method.invoke(this,args);
        }else{
            return sqlSession.selectOne(method.getName(),args);
        }
    }
}
