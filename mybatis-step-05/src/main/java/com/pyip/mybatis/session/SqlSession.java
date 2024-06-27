package com.pyip.mybatis.session;

public interface SqlSession {
    // 根据指定的SqlID获取一条记录的封装对象
    <T> T selectOne(String statement);

    // 根据指定的SqlID获取一条记录的封装对象，只不过这个方法容许我们可以给sql传递一些参数
    // 一般在实际使用中，这个参数传递的是pojo，或者Map或者ImmutableMap
    <T> T selectOne(String statement, Object parameter);

    //得到配置
    Configuration getConfiguration();

    //得到映射器，这个巧妙的使用了泛型，使得类型安全
    <T> T getMapper(Class<T> type);
}
