### 本章总结
#### 前文问题
- 耦合问题
  - 在执行sql语句时，前文是放在一起执行的如下方法
  
  - ```java
        public <T> T selectOne(String statement, Object parameter) {
            try {
                MappedStatement mappedStatement = configuration.getMappedStatement(statement);
                Environment environment = configuration.getEnvironment();
    
                Connection connection = environment.getDataSource().getConnection();
    
                BoundSql boundSql = mappedStatement.getBoundSql();
                PreparedStatement preparedStatement = connection.prepareStatement(boundSql.getSql());
                preparedStatement.setLong(1, Long.parseLong(((Object[]) parameter)[0].toString()));
                ResultSet resultSet = preparedStatement.executeQuery();
    //            connection.close(); close是放回连接池，重新get会是同一个
    
                List<T> objList = resultSet2Obj(resultSet, Class.forName(boundSql.getResultType()));
                return objList.get(0);
            }
        }
    ```
  
  - 问题：所有的sql语句执行都要经历这几个步骤，获取Connection,获取BoundSql信息，创建预处理声明PreparedStatement,设置相应的参数，执行查询，处理结果集
  - 这些步骤是通用的，我们要降低程序之间的耦合，可以将这些处理问题抽取出来，所有的的执行细节都由下游来完成即可

#### 执行器

- 只针对对数据库的操作，统一处理所有的sql语句

- |           类名           |       用途       |  类型  |         继承         | 实现             |                            方法名                            |
  | :----------------------: | :--------------: | :----: | :------------------: | ---------------- | :----------------------------------------------------------: |
  |      ResultHandler       |    结果处理器    |  接口  |                      |                  |                     void handleResult()                      |
  |         Executor         |      执行器      |  接口  |                      |                  | <E> List<E> query(MappedStatement ms, Object parameter, ResultHandler resultHandler, BoundSql boundSql);  Transaction getTransaction();  void commit(boolean required);  void rollback(boolean required);  void close(boolean forceRollback); |
  |       BaseExecutor       |    基础执行器    | 抽象类 |                      | Executor         |                                                              |
  |      SimpleExecutor      |    一般执行器    |   类   |     BaseExecutor     |                  |                                                              |
  |     StatementHandler     |    声明处理器    |  接口  |                      |                  | Statement prepare(Connection connection);  void parameterize(Statement statement) ; <E> List<E> query(Statement statement, ResultHandler resultHandler); |
  |   BaseStatementHandler   |  基础声明处理器  | 抽象类 |                      | StatementHandler |                                                              |
  | PreparedStatementHandler | 预处理声明处理器 |   类   | BaseStatementHandler |                  |                                                              |
  |  SimpleStatementHandler  |  一般声明处理器  |   类   | BaseStatementHandler |                  |                                                              |
  |     ResultSetHandler     |   结果集处理器   |  接口  |                      |                  |        <E> List<E> handleResultSets(Statement stmt);         |
  | DefaultResultSetHandler  | 默认结果集处理器 |   类   |                      | ResultSetHandler |                                                              |

- 一般方法执行流程

  - userDao为代理类对象

  - ```java
    User user = userDao.queryUserInfoById(1L);
    // MapperProxy
    final MapperMethod mapperMethod = cachedMapperMethod(method);
    return mapperMethod.execute(sqlSession, args);
    // MapperMethod
    case SELECT:
        result = sqlSession.selectOne(command.getName(), args);
        break;
    return result;
    ```

  - sqlSession.SelectOne(name,args)调用以下方法

  - ```java
    MappedStatement ms = configuration.getMappedStatement(statement);
    List<T> list = executor.query(ms, parameter, Executor.NO_RESULT_HANDLER, ms.getBoundSql());
    return list.get(0);
    ```

    - 所有的执行细节交给executor.query()执行

    - ```java
      public <T> T selectOne(String statement, Object parameter) {
          MappedStatement ms = configuration.getMappedStatement(statement);
          List<T> list = executor.query(ms, parameter, Executor.NO_RESULT_HANDLER, ms.getBoundSql());
          return list.get(0);
      }
      // executor是SimpleExecutor的父类的实现接口
      // SimpleExecutor
          protected <E> List<E> doQuery(MappedStatement ms, Object parameter, ResultHandler resultHandler, BoundSql boundSql) {
              try {
                  Configuration configuration = ms.getConfiguration();
                  StatementHandler handler = configuration.newStatementHandler(this, ms, parameter, resultHandler, boundSql);
                  Connection connection = transaction.getConnection();
                  Statement stmt = handler.prepare(connection);
                  handler.parameterize(stmt);
                  return handler.query(stmt, resultHandler);
              } catch (SQLException e) {
                  e.printStackTrace();
                  return null;
              }
          }
      ```

    - StatementHandler handler = configuration.newStatementHandler(this, ms, parameter, resultHandler, boundSql); StateHandler是PreparedStatementHandler，即是StatementHandler的子类

    - ```java
      // handler.query(stmt, resultHandler);
      // PreParedStatementHandler.java
      public <E> List<E> query(Statement statement, ResultHandler resultHandler) throws SQLException {
          String sql = boundSql.getSql();
          statement.execute(sql);
          return resultSetHandler.handleResultSets(statement);
      }
      // DefaultResultSetHandler.java
      public <E> List<E> handleResultSets(Statement stmt) throws SQLException {
              ResultSet resultSet = stmt.getResultSet();
              try {
                  return resultSet2Obj(resultSet, Class.forName(boundSql.getResultType()));
              } catch (ClassNotFoundException e) {
                  e.printStackTrace();
                  return null;
              }
          }
      // resultSet2Obj方法是对结果进行反射封装为List<E>
      // resultSet2Obj(resultSet, Class.forName(boundSql.getResultType()));
      ```