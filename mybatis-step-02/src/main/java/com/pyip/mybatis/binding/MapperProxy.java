package com.pyip.mybatis.binding;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;

public class MapperProxy<T> implements InvocationHandler, Serializable {
    private static final long serialVersionUID = -6424540398559729838L;
    private Map<String,String> sqlSession;
    private final Class<T> mapperInterface;
    public MapperProxy(Map<String,String> sqlSession,Class<T> mapperInterface){
        this.sqlSession = sqlSession;
        this.mapperInterface = mapperInterface;
    }
//    String res = userDao.queryUserName("10001");
    /**
     * proxy: Method threw "java.lang.reflect.UndeclaredThrowableException' exception.
     *        Cannot evaluate com.sun.proxy.$Proxy2.toString()
     *        MapperProxy
     *method: queryUserName(String)
     * args: Object[1] ->0 : 10001
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Class<?> declaringClass = method.getDeclaringClass(); // name: com.pyip.mybatis.test.dao.IUserDao
        if(Object.class.equals(declaringClass)){
            return method.invoke(proxy,args);
        }else{
            return "你的操作被代理了！"+sqlSession.get(mapperInterface.getName()+"."+method.getName());
        }
    }
}
