### 本章总结

#### 本章小结

- 对功能进行扩展，添加更新，删除与插入操作

- 更新删除添加操作，本质上全是调用的更新方法

- ```java
  @Override
  public int insert(String statement, Object parameter) {
      // 在 Mybatis 中 insert 调用的是 update
      return update(statement, parameter);
  }
  
  @Override
  public int update(String statement, Object parameter) {
      MappedStatement ms = configuration.getMappedStatement(statement);
      return executor.update(ms, parameter);
  }
  
  @Override
  public Object delete(String statement, Object parameter) {
      return update(statement, parameter);
  }
  
  @Override
  public void commit() {
      executor.commit(true);
  }
  ```

- doUpdate

- ```java
  protected int doUpdate(MappedStatement ms, Object parameter) throws SQLException {
      Statement stmt = null;
      try {
          Configuration configuration = ms.getConfiguration();
          // 新建一个 StatementHandler
          StatementHandler handler = configuration.newStatementHandler(this, ms, parameter, RowBounds.DEFAULT, null, null);
          // 准备语句
          stmt = prepareStatement(handler);
          // StatementHandler.update
          return handler.update(stmt);
      } finally {
          closeStatement(stmt);
      }
  }
  
      public int update(Statement statement) throws SQLException {
          PreparedStatement ps = (PreparedStatement) statement;
          ps.execute();
          return ps.getUpdateCount();
      }
  ```

- 对不同的方法调用不同的语句

- ```java
  switch (command.getType()) {
      case INSERT: {
          Object param = method.convertArgsToSqlCommandParam(args);
          result = sqlSession.insert(command.getName(), param);
          break;
      }
      case DELETE: {
          Object param = method.convertArgsToSqlCommandParam(args);
          result = sqlSession.delete(command.getName(), param);
          break;
      }
      case UPDATE: {
          Object param = method.convertArgsToSqlCommandParam(args);
          result = sqlSession.update(command.getName(), param);
          break;
      }
      case SELECT: {
          Object param = method.convertArgsToSqlCommandParam(args);
          if (method.returnsMany) { // 本质上是返回list还是单个对象
              result = sqlSession.selectList(command.getName(), param);
          } else {
              result = sqlSession.selectOne(command.getName(), param);
          }
          break;
      }
      default:
          throw new RuntimeException("Unknown execution method for: " + command.getName());
  }
  ```