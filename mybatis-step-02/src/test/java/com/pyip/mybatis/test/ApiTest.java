package com.pyip.mybatis.test;

import com.pyip.mybatis.binding.MapperProxyFactory;
import com.pyip.mybatis.test.dao.IUserDao;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

public class ApiTest {
    private Logger logger = LoggerFactory.getLogger(ApiTest.class);
    @Test
    public void testMapperProxyFactory(){
        MapperProxyFactory<IUserDao> factory = new MapperProxyFactory<>(IUserDao.class);
        Map<String, String> sqlSession = new HashMap<>();

        sqlSession.put("com.pyip.mybatis.test.dao.IUserDao.queryUserName",
                "模拟执行Mapper.xml中SQL语句的操作：查询用户姓名");
        sqlSession.put("com.pyip.mybatis.test.dao.IUserDao.queryUserAge",
                "模拟执行Mapper.xml中SQL语句的操作：查询用户年龄");
        // 通过代理工厂生成指定T(IUserDao)对象
        IUserDao userDao = factory.newInstance(sqlSession);

        String res = userDao.queryUserName("10001"); // 执行代理类的invoke方法
        logger.info("测试结果：{}",res);
    }
    @Test
    public void test_proxy_class() {
        IUserDao userDao = (IUserDao) Proxy.newProxyInstance(
                Thread.currentThread().getContextClassLoader(),
                new Class[]{IUserDao.class}, (proxy, method, args) -> "你的操作被代理了！");
        String result = userDao.queryUserName("10001");
        System.out.println("测试结果：" + result);
    }
}
