package com.pyip.mybatis;

/**
 * sql session会话生产的实现类
 */
public class DefaultSqlSessionFactory implements SqlSessionFactory{
    private final Configuration configuration;
    // 通过配置文件进行构造一个默认的sql session会话工厂
    public DefaultSqlSessionFactory(Configuration configuration){
        this.configuration = configuration;
    }
    // DefaultSqlSessionFactory构造函数向下传递了Configuration配置文件，
    // 该配置文件中包括Connection connection,
    // Map<string,String> dataSource,Map<String,XNode> mapperElement.
    @Override
    public SqlSession openSession() {
        return new DefaultSqlSession(configuration.connection,configuration.mapperElement);
    }
}
