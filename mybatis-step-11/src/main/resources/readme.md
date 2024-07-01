### 本章总结

#### 问题分析

- 在前一章我们使用策略模式解决了预处理语句封装参数时的硬编码耦合问题，但是对封装的结果我们又要怎么来解决问题呢？

- ```java
  private <T> List<T> resultSet2Obj(ResultSet resultSet, Class<?> clazz) {
      List<T> list = new ArrayList<>();
      try {
          ResultSetMetaData metaData = resultSet.getMetaData();
          int columnCount = metaData.getColumnCount();
          // 每次遍历行值
          while (resultSet.next()) {
              T obj = (T) clazz.newInstance();
              for (int i = 1; i <= columnCount; i++) {
                  Object value = resultSet.getObject(i);
                  String columnName = metaData.getColumnName(i);
                  String setMethod = "set" + columnName.substring(0, 1).toUpperCase() + columnName.substring(1);
                  Method method;
                  if (value instanceof Timestamp) {
                      method = clazz.getMethod(setMethod, Date.class);
                  } else {
                      method = clazz.getMethod(setMethod, value.getClass());
                  }
                  method.invoke(obj, value);
              }
              list.add(obj);
          }
      } catch (Exception e) {
          e.printStackTrace();
      }
      return list;
  }
  ```

#### 本章小结

- 流程分析

  - 对结果集Set进行指定对象或基本类型的封装

  - ```java
    // PreparedStatementHandler extends BaseStatementHandler
    return resultSetHandler.<E>handleResultSets(ps);
    // DefaultResultSetHandler implements ResultSetHandler
    // 根据ResultSetWrapper的映射规则，对每个数据集结果进行封装
        public List<Object> handleResultSets(Statement stmt) throws SQLException {
            final List<Object> multipleResults = new ArrayList<>();
            int resultSetCount = 0;
            ResultSetWrapper rsw = new ResultSetWrapper(stmt.getResultSet(), configuration);
            List<ResultMap> resultMaps = mappedStatement.getResultMaps();
            while (rsw != null && resultMaps.size() > resultSetCount) {
                ResultMap resultMap = resultMaps.get(resultSetCount);
                handleResultSet(rsw, resultMap, multipleResults, null);
                rsw = getNextResultSet(stmt);
                resultSetCount++;
            }
            return multipleResults.size() == 1 ? (List<Object>) multipleResults.get(0) : multipleResults;
        }
    	// ResultSetWrapper rsw = new ResultSetWrapper(stmt.getResultSet(), configuration);
    	    public ResultSetWrapper(ResultSet rs, Configuration configuration) throws SQLException {
            super();
            this.typeHandlerRegistry = configuration.getTypeHandlerRegistry();
            this.resultSet = rs;
            final ResultSetMetaData metaData = rs.getMetaData();
            final int columnCount = metaData.getColumnCount();
            for (int i = 1; i <= columnCount; i++) {
                columnNames.add(metaData.getColumnLabel(i));
                jdbcTypes.add(JdbcType.forCode(metaData.getColumnType(i)));
                classNames.add(metaData.getColumnClassName(i));
            }
        }
    
    ```