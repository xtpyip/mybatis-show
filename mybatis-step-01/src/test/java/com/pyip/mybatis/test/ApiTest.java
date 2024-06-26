package com.pyip.mybatis.test;

import com.alibaba.fastjson.JSON;
import com.pyip.mybatis.*;
import com.pyip.mybatis.test.po.User;
import org.junit.Test;

import java.io.Reader;

// 单元测试
public class ApiTest {

    @Test
    public void queryUserInfoById(){
        String resource = "mybatis-config-datasource.xml";
        Reader reader;
        try {
            reader = Resources.getResourceAsReader(resource);
            SqlSessionFactory sqlMapper = new SqlSessionFactoryBuilder().build(reader);
            SqlSession session = sqlMapper.openSession();
            try {
                User user = session.selectOne("com.pyip.mybatis.test.dao.IUserDao.queryUserInfoById", 1L);
                System.out.println(JSON.toJSONString(user));
            }finally {
                session.close();
                reader.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
