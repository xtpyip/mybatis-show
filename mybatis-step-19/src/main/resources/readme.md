### 本章总结

#### 二级缓存

- 二级缓存的实现是在一个mapper中的，不同会话也能查询到缓存的信息的
- 如何实现二级缓存是一个重难点

#### 二级缓存实现

- 流程，配置缓存是否开启及缓存的级别

- ```xml
  <settings>
      <!-- 全局缓存：true/false -->
      <setting name="cacheEnabled" value="true"/>
      <!--缓存级别：SESSION/STATEMENT-->
      <setting name="localCacheScope" value="STATEMENT"/>
  </settings>
  // 指定mapper的存储策略
  <cache eviction="FIFO" flushInterval="600000" size="512" readOnly="true"/>
  ```

- 执行查询语句

- ```java
  // cachingExecutor
  public <E> List<E> query(MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler) throws SQLException {
      // 1. 获取绑定SQL
      BoundSql boundSql = ms.getBoundSql(parameter);
      // 2. 创建缓存Key
      CacheKey key = createCacheKey(ms, parameter, rowBounds, boundSql);
      return query(ms, parameter, rowBounds, resultHandler, key, boundSql);
  }
  ```

  - 使用TransactionalCacheManager对cache进行缓存

  - ```java
    public <E> List<E> query(MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, CacheKey key, BoundSql boundSql) throws SQLException {
        Cache cache = ms.getCache();
        if (cache != null) {
            flushCacheIfRequired(ms);
            if (ms.isUseCache() && resultHandler == null) {
                @SuppressWarnings("unchecked")
                List<E> list = (List<E>) tcm.getObject(cache, key);
                if (list == null) {
                    list = delegate.<E>query(ms, parameter, rowBounds, resultHandler, key, boundSql);
                    // cache：缓存队列实现类，FIFO
                    // key：哈希值 [mappedStatementId + offset + limit + SQL + queryParams + environment]
                    // list：查询的数据
                    tcm.putObject(cache, key, list);
                }
                // 打印调试日志，记录二级缓存获取数据
                if (logger.isDebugEnabled() && cache.getSize() > 0) {
                    logger.debug("二级缓存：{}", JSON.toJSONString(list));
                }
                return list;
            }
        }
        return delegate.<E>query(ms, parameter, rowBounds, resultHandler, key, boundSql);
    }
    ```