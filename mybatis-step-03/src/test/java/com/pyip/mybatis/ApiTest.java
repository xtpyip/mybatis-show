package com.pyip.mybatis;

import com.pyip.mybatis.binding.MapperRegistry;
import com.pyip.mybatis.dao.IUserDao;
import com.pyip.mybatis.session.SqlSession;
import com.pyip.mybatis.session.SqlSessionFactory;
import com.pyip.mybatis.session.defaults.DefaultSqlSessionFactory;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApiTest {
    private Logger logger = LoggerFactory.getLogger(ApiTest.class);

    @Test
    public void test_MapperRegistry(){
        // 注册mapper
        MapperRegistry registry = new MapperRegistry();
        registry.addMappers("com.pyip.mybatis.dao");
        // 从SqlSession工厂获取session
        SqlSessionFactory sqlSessionFactory = new DefaultSqlSessionFactory(registry);
        SqlSession sqlSession = sqlSessionFactory.openSession();
        // 获取映射器对象
        IUserDao userDao = sqlSession.getMapper(IUserDao.class);
        // 测试映射器对象（代理类）
        String res = userDao.queryUserName("10001");
        logger.info("测试结束：{}",res);
    }
}
