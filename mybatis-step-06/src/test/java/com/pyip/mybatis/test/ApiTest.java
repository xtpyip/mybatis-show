package com.pyip.mybatis.test;

import com.pyip.mybatis.datasource.pooled.PooledDataSource;
import com.pyip.mybatis.datasource.unpooled.UnpooledDataSource;
import com.pyip.mybatis.io.Resources;
import com.pyip.mybatis.session.SqlSession;
import com.pyip.mybatis.session.SqlSessionFactory;
import com.pyip.mybatis.session.SqlSessionFactoryBuilder;
import com.alibaba.fastjson.JSON;
import com.pyip.mybatis.test.dao.IUserDao;
import com.pyip.mybatis.test.po.User;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author 小傅哥，微信：fustack
 * @description 单元测试
 * @github https://github.com/fuzhengwei
 * @Copyright 公众号：bugstack虫洞栈 | 博客：https://bugstack.cn - 沉淀、分享、成长，让自己和他人都能有所收获！
 */
public class ApiTest {

    private Logger logger = LoggerFactory.getLogger(ApiTest.class);

    @Test
    public void test_SqlSessionFactory() throws IOException {
        // 1. 从SqlSessionFactory中获取SqlSession
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(Resources.getResourceAsReader("mybatis-config-datasource.xml"));
        SqlSession sqlSession = sqlSessionFactory.openSession();

        // 2. 获取映射器对象
        IUserDao userDao = sqlSession.getMapper(IUserDao.class);

        // 3. 测试验证
        for (int i = 0; i < 50; i++) {
            User user = userDao.queryUserInfoById(1L);
            logger.info("测试结果：{}", JSON.toJSONString(user));
        }
    }

    @Test
    public void test_pooled() throws SQLException, InterruptedException {
        PooledDataSource pooledDataSource = new PooledDataSource();
        pooledDataSource.setDriver("com.mysql.cj.jdbc.Driver");
        pooledDataSource.setUrl("jdbc:mysql://127.0.0.1:3306/mybatis_show?useUnicode=true");
        pooledDataSource.setUsername("root");
        pooledDataSource.setPassword("10086");
        // 持续获得链接
        while (true) {
            Connection connection = pooledDataSource.getConnection();
            System.out.println(connection);
            Thread.sleep(1000);
            // 注释掉/不注释掉测试
            connection.close();
        }
        // 使用池化技术
        //com.mysql.cj.jdbc.ConnectionImpl@9d5509a
        //16:11:53.944 [main] INFO  c.p.m.d.pooled.PooledDataSource - Returned connection 164974746 to pool.
        //16:11:53.947 [main] INFO  c.p.m.d.pooled.PooledDataSource - Checked out connection 164974746 from pool.
        //com.mysql.cj.jdbc.ConnectionImpl@9d5509a
        //16:11:54.948 [main] INFO  c.p.m.d.pooled.PooledDataSource - Returned connection 164974746 to pool.
        //16:11:54.948 [main] INFO  c.p.m.d.pooled.PooledDataSource - Checked out connection 164974746 from pool.
    }

    @Test
    public void test_unpooled() throws SQLException, InterruptedException {
        UnpooledDataSource unpooledDataSource = new UnpooledDataSource();
        unpooledDataSource.setDriver("com.mysql.cj.jdbc.Driver");
        unpooledDataSource.setUrl("jdbc:mysql://127.0.0.1:3306/mybatis_show?useUnicode=true");
        unpooledDataSource.setUsername("root");
        unpooledDataSource.setPassword("10086");
        // 持续获得链接
        while (true) {
            Connection connection = unpooledDataSource.getConnection();
            System.out.println(connection);
            Thread.sleep(1000);
            // 注释掉/不注释掉测试
            connection.close();
        }
//        connection 一直在变（未用池化技术）
        //com.mysql.cj.jdbc.ConnectionImpl@3fc2959f
        //com.mysql.cj.jdbc.ConnectionImpl@385c9627
        //com.mysql.cj.jdbc.ConnectionImpl@217ed35e
        //com.mysql.cj.jdbc.ConnectionImpl@229f66ed
        //com.mysql.cj.jdbc.ConnectionImpl@662ac478
    }

}
