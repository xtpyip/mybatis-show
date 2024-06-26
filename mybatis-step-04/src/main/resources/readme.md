### 本章分析
- 难点
  - 类与类是嵌套的，可能有影响
    - Configuration类包含两个属性MapperRegistry及Map<String, MappedStatement>
    - MappedStatement类包含Configuration,id,sqlCommandType,parameterType,resultType,sql及Map<Integer, String> parameter
    - Configuration与MappedStatement是互相嵌套的
  - Configuration类是包含了全局的映射注册机和所有以<b>命名空间+.+id</b>的sql语句包装的类
  - MappedStatement类包含的Configuration是由构造者创建的默认是空的，也可以set与get
- 流程分析
  - 复杂（!!!）
  - Reader reader = Resources.getResourceAsReader("mybatis-config-datasource.xml");先得到mybatis-config-datasource文件的Reader
  - SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);通过这个reader来创建SqlSessionFactory
    - XMLConfigBuilder xmlConfigBuilder = new XMLConfigBuilder(reader);得到初始化的Configuration与xml文档的root标签
      - xmlConfigBuilder.parse(); 为所有的<mapper resource="mapper/User_Mapper.xml"/>标签生成映射器存入Configuration的MapperRegistry中,及将所有的MappedStatement放入到Configuration的Map集合中
      - new DefaultSqlSessionFactory(configuration);将上述装得的Configuration进行向下传递
  - SqlSession sqlSession = sqlSessionFactory.openSession();
    - return new DefaultSqlSession(configuration);将configuration继续向下传递，返回SqlSession对象
  - IUserDao userDao = sqlSession.getMapper(IUserDao.class);
    - return configuration.getMapper(type, this); 返回映射器对象
      - return mapperRegistry.getMapper(type, sqlSession); 通过全局映射注册机得到对应的映射器工厂类
        - return mapperProxyFactory.newInstance(sqlSession); 通过工厂类实例化映射器
          - final MapperProxy<T> mapperProxy = new MapperProxy<>(sqlSession, mapperInterface, methodCache); 实例化mapperProxy
          - return (T) Proxy.newProxyInstance(mapperInterface.getClassLoader(), new Class[]{mapperInterface}, mapperProxy); 返回代理类实例对象
  - String res = userDao.queryUserInfoById("10001");
    - final MapperMethod mapperMethod = cachedMapperMethod(method);将指定类名.方法名(参数名)转化为对应的MapperMethod
      - mapperMethod = new MapperMethod(mapperInterface, method, sqlSession.getConfiguration()); 将所有不存在于map中的方法转化为MapperMethod
      - methodCache.put(method, mapperMethod);并存储进map中
    - return mapperMethod.execute(sqlSession, args); 执行execute方法
      - case SELECT: result = sqlSession.selectOne(command.getName(), args); 执行这个类的方法并将结果存于result中
        - ```java
           public <T> T selectOne(String statement, Object parameter) {
           MappedStatement mappedStatement = configuration.getMappedStatement(statement);
          return (T) ("你的操作被代理了！" + "\n方法：" + statement + "\n入参："
           + parameter + "\n待执行SQL：" + mappedStatement.getSql());
           }```
      - return result; 返回result
  - logger.info("测试结果：{}", res); 打印结果 