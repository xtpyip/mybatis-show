### 本章总结
#### 本章重点
- 缓存的实现，添加，删除，查找等
- 正常使用时，应该当查询开始时，查看缓存，若存在，则返回，不存在，则查询数据库，结束后存放缓存
- ```java
  @Test
  public void test_queryActivityById() throws IOException {
      // 1. 从SqlSessionFactory中获取SqlSession
      SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(Resources.getResourceAsReader("mybatis-config-datasource.xml"));
      SqlSession sqlSession = sqlSessionFactory.openSession();
      // 2. 获取映射器对象
      IActivityDao dao = sqlSession.getMapper(IActivityDao.class);
      // 3. 测试验证
      Activity req = new Activity();
      req.setActivityId(100001L);
      logger.info("测试结果：{}", JSON.toJSONString(dao.queryActivityById(req)));
      // 测试时，可以分别开启对应的注释，验证功能逻辑
      // sqlSession.commit();
      // sqlSession.clearCache();
      // sqlSession.close();
      logger.info("测试结果：{}", JSON.toJSONString(dao.queryActivityById(req)));
  }
  ```

- req.setActivityId(100001L);

  - 查询指定sql

  - ```java
    public <E> List<E> query(MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler) throws SQLException {
        // 1. 获取绑定SQL
        BoundSql boundSql = ms.getBoundSql(parameter);
        // 2. 创建缓存Key
        CacheKey key = createCacheKey(ms, parameter, rowBounds, boundSql);
        return query(ms, parameter, rowBounds, resultHandler, key, boundSql);
    }
    ```

  - 缓存的Key生成，（mappedstatementid+offset+limit+SQL+queryParams+environment）

  - ```java
    @Override
    public <E> List<E> query(MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, CacheKey key, BoundSql boundSql) throws SQLException {
        if (closed) {
            throw new RuntimeException("Executor was closed.");
        }
        // 清理局部缓存，查询堆栈为0则清理。queryStack 避免递归调用清理
        if (queryStack == 0 && ms.isFlushCacheRequired()) {
            clearLocalCache();
        }
        List<E> list;
        try {
            queryStack++;
            // 根据cacheKey从localCache中查询数据
            list = resultHandler == null ? (List<E>) localCache.getObject(key) : null;
            if (list == null) {
                // 未查到，则从数据库中查询
                list = queryFromDatabase(ms, parameter, rowBounds, resultHandler, key, boundSql);
            }
        } finally {
            queryStack--;
        }
        if (queryStack == 0) {
            // 如果不使用一级缓存，则清除其内的缓存
            if (configuration.getLocalCacheScope() == LocalCacheScope.STATEMENT) {
                clearLocalCache();
            }
        }
        return list;
    }
    ```

  - 从数据库中查找，将结果存入缓存中

  - ```java
    private <E> List<E> queryFromDatabase(MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, CacheKey key, BoundSql boundSql) throws SQLException {
        List<E> list;
        localCache.putObject(key, ExecutionPlaceholder.EXECUTION_PLACEHOLDER);
        try {
            list = doQuery(ms, parameter, rowBounds, resultHandler, boundSql);
        } finally {
            localCache.removeObject(key);
        }
        // 存入缓存
        localCache.putObject(key, list);
        return list;
    }
    ```

- ```java
  @Override
  public <E> List<E> query(MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, CacheKey key, BoundSql boundSql) throws SQLException {
      if (closed) {
          throw new RuntimeException("Executor was closed.");
      }
      // 清理局部缓存，查询堆栈为0则清理。queryStack 避免递归调用清理
      if (queryStack == 0 && ms.isFlushCacheRequired()) {
          clearLocalCache();
      }
      List<E> list;
      try {
          queryStack++;
          // 根据cacheKey从localCache中查询数据
          list = resultHandler == null ? (List<E>) localCache.getObject(key) : null; // 查到了，直接从缓存中拿指定数据
          if (list == null) {
              // 未查到，则从数据库中查询
              list = queryFromDatabase(ms, parameter, rowBounds, resultHandler, key, boundSql);
          }
      } finally {
          queryStack--;
      }
      if (queryStack == 0) {
          // 如果不使用一级缓存，则清除其内的缓存
          if (configuration.getLocalCacheScope() == LocalCacheScope.STATEMENT) {
              clearLocalCache();
          }
      }
      return list;
  }
  ```

  