### 本章总结 

- 如何在insert后实现返回id自增值

- ```xml
  <insert id="insert" parameterType="com.pyip.mybatis.test.po.Activity">
      INSERT INTO activity
      (activity_id, activity_name, activity_desc, create_time, update_time)
      VALUES (#{activityId}, #{activityName}, #{activityDesc}, now(), now())
      <selectKey keyProperty="id" order="AFTER" resultType="long">
          SELECT LAST_INSERT_ID()
      </selectKey>
  </insert>
  ```

- 只有对insert操作才会有selectKey标签，才有意义

- ```java
  // 属性标记【仅对 insert 有用】, MyBatis 会通过 getGeneratedKeys 或者通过 insert 语句的 selectKey 子元素设置它的值 step-14 新增
  String keyProperty = element.attributeValue("keyProperty");
  KeyGenerator keyGenerator = null;
  String keyStatementId = id + SelectKeyGenerator.SELECT_KEY_SUFFIX;
  keyStatementId = builderAssistant.applyCurrentNamespace(keyStatementId, true);
  if (configuration.hasKeyGenerator(keyStatementId)) {
      keyGenerator = configuration.getKeyGenerator(keyStatementId);
  } else {
      keyGenerator = configuration.isUseGeneratedKeys() && SqlCommandType.INSERT.equals(sqlCommandType) ? new Jdbc3KeyGenerator() : new NoKeyGenerator();
  }
  ```

- 执行插入即更新时，添加keyGenerator

- ```java
  KeyGenerator keyGenerator = mappedStatement.getKeyGenerator();
  keyGenerator.processAfter(executor, mappedStatement, ps, parameterObject);
  /**
       * 针对Sequence主键而言，在执行insert sql前必须指定一个主键值给要插入的记录，
       * 如Oracle、DB2，KeyGenerator提供了processBefore()方法。
       */
      void processBefore(Executor executor, MappedStatement ms, Statement stmt, Object parameter);
  
      /**
       * 针对自增主键的表，在插入时不需要主键，而是在插入过程自动获取一个自增的主键，
       * 比如MySQL、PostgreSQL，KeyGenerator提供了processAfter()方法
       */
      void processAfter(Executor executor, MappedStatement ms, Statement stmt, Object parameter);
  
  ```

- 生成id,要在一个会话中执行

- ```java
  public void processAfter(Executor executor, MappedStatement ms, Statement stmt, Object parameter) {
      if (!executeBefore) {
          processGeneratedKeys(executor, ms, parameter);
      }
  }
  
  private void processGeneratedKeys(Executor executor, MappedStatement ms, Object parameter) {
      try {
          if (parameter != null && keyStatement != null && keyStatement.getKeyProperties() != null) {
              String[] keyProperties = keyStatement.getKeyProperties();
              final Configuration configuration = ms.getConfiguration();
              final MetaObject metaParam = configuration.newMetaObject(parameter);
              if (keyProperties != null) {
                  Executor keyExecutor = configuration.newExecutor(executor.getTransaction());
                  List<Object> values = keyExecutor.query(keyStatement, parameter, RowBounds.DEFAULT, Executor.NO_RESULT_HANDLER);
                  if (values.size() == 0) {
                      throw new RuntimeException("SelectKey returned no data.");
                  } else if (values.size() > 1) {
                      throw new RuntimeException("SelectKey returned more than one value.");
                  } else {
                      MetaObject metaResult = configuration.newMetaObject(values.get(0));
                      if (keyProperties.length == 1) {
                          if (metaResult.hasGetter(keyProperties[0])) {
                              setValue(metaParam, keyProperties[0], metaResult.getValue(keyProperties[0]));
                          } else {
                              setValue(metaParam, keyProperties[0], values.get(0));
                          }
                      } else {
                          handleMultipleProperties(keyProperties, metaParam, metaResult);
                      }
                  }
              }
          }
      } catch (Exception e) {
          throw new RuntimeException("Error selecting key or setting result to parameter object. Cause: " + e);
      }
  }
  ```