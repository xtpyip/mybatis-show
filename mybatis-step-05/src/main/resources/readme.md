### 本章总结

#### 难点

- 简介
  - 本章主内容是数据源的配置与使用
  
- 核心配置类
  - Configuration
    - 属性
      - Environment environment 环境信息
      - MapperRegistry mapperRegistry 映射注册机
      - Map<String, MappedStatement> mappedStatements 可执行sql封装数据
      - TypeAliasRegistry typeAliasRegistry 类型别名注册机（数据源）
  - Environment
    - 属性
      - String id 唯一Id
      - TransactionFactory transactionFactory 事务工厂（接口）
      - DataSource dataSource 数据源（接口）
    - 帮助理解：所有的事务都是针对于不同的数据源的，可能是Druid,也可能是JDBC等
      - 数据源与事务工厂来源于配置文件 
      - ```xml
                <environment id="development">
                    <transactionManager type="JDBC"/>
                    <dataSource type="DRUID">
                        <property name="driver" value="com.mysql.cj.jdbc.Driver"/>
                        <property name="url" value="jdbc:mysql://127.0.0.1:3306/mybatis_show?useUnicode=true"/>
                        <property name="username" value="root"/>
                        <property name="password" value="10086"/>
                    </dataSource>
                </environment>
  - TypeAliasRegistry typeAliasRegistry
  
    - 属性
      - Map<String, Class<?>> TYPE_ALIASES 所有的类名及注册类，如
      - "string":java.lang.String.class
      - "JDBC",JdbcTransactionFactory.Class
      - "DRUID":DruidDataSourceFactory.class
  - MapperRegistry mapperRegistry
  
    - 属性
      - Configuration configuration 初始化所有的配置信息
      - Map<Class<?>, MapperProxyFactory<?>> knownMappers 所有接口类及其代理工厂类（映射注册机）
  
  - MappedStatement mappedStatement
    - 属性
      - Configuration configuration 所有的配置信息
      - String id 命令空间.id
      - SqlCommandType sqlCommandType
        - 属性
          - 枚举： UNKNOW,INSERT,UPDATE,DELETE,SELECT
      - BoundSql boundSql
        - 属性
          - String sql sql语句，将所有的#{}替换为?号
          - Map<Integer,String> parameterMappings 记录的所有#{}位置及内部的映射关系
          - String parameterType  参数类型
          - String resultType  结果类型
  - TransactionIsolationLevel transactionIsolationLevel
    - 属性（事务隔离级别）
      - 枚举：NONE，READ_COMMITTED，READ_UNCOMMITTED，REPEATABLE_READ，SERIALIZABLE

#### 流程分析

- ```java
          SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(Resources.getResourceAsReader("mybatis-config-datasource.xml"));
          SqlSession sqlSession = sqlSessionFactory.openSession();
  
          // 2. 获取映射器对象
          IUserDao userDao = sqlSession.getMapper(IUserDao.class);
  
          // 3. 测试验证
          User user = userDao.queryUserInfoById(1L);
          logger.info("测试结果：{}", JSON.toJSONString(user));
  ```

- SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(Resources.getResourceAsReader("mybatis-config-datasource.xml"))

  - 将mybatis-config-datasource.xml文件转化为Reader类，并使用SqlSessionFactoryBuilder来创建

  - parse函数

    - ```java
      XMLConfigBuilder xmlConfigBuilder = new XMLConfigBuilder(reader);
      return build(xmlConfigBuilder.parse());
      ```

    - XMLConfigBuilder对Reader进行解析，parse会将所有的信息存储进Configuration类中（传递到下面）

      - 以下两句会将xml文件中的所有有用数据存储入Configuration中

      - ```java
        // 环境
        environmentsElement(root.element("environments"));
        // 解析映射器
        mapperElement(root.element("mappers"));
        ```

      - environmentsElement对以下数据解析，存储进Environment中，并存入Configuration中

      - ```
        <environments default="development">
            <environment id="development">
                <transactionManager type="JDBC"/>
                <dataSource type="DRUID">
                    <property name="driver" value="com.mysql.cj.jdbc.Driver"/>
                    <property name="url" value="jdbc:mysql://127.0.0.1:3306/mybatis_show?useUnicode=true"/>
                    <property name="username" value="root"/>
                    <property name="password" value="10086"/>
                </dataSource>
            </environment>
        </environments>
        ```

      - mapperElement是对xml中的mappers标签指引的所有XXX_Mapper.xml进行解析，并存储在MappedStatement中，还把所有的接口代理工厂类放入Configuration中

      - ```xml
        <!-- mybatis-config-datasource.xml -->   
        <mappers>
                <mapper resource="mapper/User_Mapper.xml"/>
            </mappers>
        <!-- XXX_Mapper.xml -->   
        <mapper namespace="com.pyip.mybatis.test.dao.IUserDao">
            <select id="queryUserInfoById" parameterType="java.lang.Long" resultType="com.pyip.mybatis.test.po.User">
                SELECT id, userId, userHead, createTime,updateTime
                FROM user
                where id = #{id}
            </select>
        </mapper>
        ```

  - build函数
    - return new DefaultSqlSessionFactory(config); 返回默认的SqlSessionFactory实现类

- SqlSession sqlSession = sqlSessionFactory.openSession(); 新建一个SqlSession实现类

- IUserDao userDao = sqlSession.getMapper(IUserDao.class);

  - 调用SqlSession实现类的方法，返回代理实现类

  - ```java
    return configuration.getMapper(type, this);
    ```

    - 调用Configuration方法，调用映射注册机

    - ```java
      return mapperRegistry.getMapper(type, sqlSession);
      ```

      - 调用映射注册机得到代理工厂类，并实例化代理类

      - ```java
        return (MapperProxyFactory<T>) knownMappers.get(type).newInstance(sqlSession)
        ```

- User user = userDao.queryUserInfoById(1L);

  - 调用代理类的invoke方法

  - ```java
    if (Object.class.equals(method.getDeclaringClass())) {
        return method.invoke(this, args);
    } else {
        final MapperMethod mapperMethod = cachedMapperMethod(method);
        return mapperMethod.execute(sqlSession, args);
    }
    ```

    - mapperMethod.execute最后执行的是默认SqlSession的实现类的selectOne方法

    - ```java
      case SELECT:
          result = sqlSession.selectOne(command.getName(), args);
      ```

      - 使用向下传递而来的Configuration类来获取到sql相关信息，执行并返回结果

      - ```java
        MappedStatement mappedStatement = configuration.getMappedStatement(statement);
        Environment environment = configuration.getEnvironment();
        
        Connection connection = environment.getDataSource().getConnection();
        
        BoundSql boundSql = mappedStatement.getBoundSql();
        PreparedStatement preparedStatement = connection.prepareStatement(boundSql.getSql());
        preparedStatement.setLong(1, Long.parseLong(((Object[]) parameter)[0].toString()));
        ResultSet resultSet = preparedStatement.executeQuery();
        
        List<T> objList = resultSet2Obj(resultSet, Class.forName(boundSql.getResultType()));
        return objList.get(0);
        ```

- logger.info("测试结果：{}", JSON.toJSONString(user));
  - 测试结果：{"createTime":1649779200000,"id":1,"updateTime":1649779200000,"userHead":"1_04","userId":"10001"}