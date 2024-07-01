### 本章总结

#### 问题分析

- 前面一章解决了解析xml时的耦合问题，这一章存在着对参数进行相应的赋值时的耦合问题，如下

- ```java
  public void parameterize(Statement statement) throws SQLException {
      PreparedStatement ps = (PreparedStatement) statement;
      ps.setLong(1, Long.parseLong(((Object[]) parameterObject)[0].toString()));
  }
  ```

- 对Long类型是setLong,对String类型是SetString等等，这里存在耦合问题

#### 本章小结

- 使用策略模式对所有的参数类型进行接口扩展，所有的类型处理器继承同一个接口，当使用ps.set参数进，根据设置的类型调用不同的类型处理器，如下

- ```java
  // TypeHandler接口
  public interface TypeHandler<T> {
      // 设置参数
      void setParameter(PreparedStatement ps, int i, T parameter, JdbcType jdbcType) throws SQLException;
  }
  // BaseTypeHandler抽象类
  public abstract class BaseTypeHandler<T> implements TypeHandler<T> {
      protected Configuration configuration;
      public void setConfiguration(Configuration configuration) {
          this.configuration = configuration;
      }
      @Override
      public void setParameter(PreparedStatement ps, int i, T parameter, JdbcType jdbcType) throws SQLException {
          // 定义抽象方法，由子类实现不同类型的属性设置
          setNonNullParameter(ps, i, parameter, jdbcType);
      }
      protected abstract void setNonNullParameter(PreparedStatement ps, int i, T parameter, JdbcType jdbcType) throws SQLException;
  }
  // LongTypeHandler实现类
  public class LongTypeHandler extends BaseTypeHandler<Long> {
      @Override
      protected void setNonNullParameter(PreparedStatement ps, int i, Long parameter, JdbcType jdbcType) throws SQLException {
          ps.setLong(i, parameter);
      }
  }
  // StringTypeHandler实现类
  public class StringTypeHandler extends BaseTypeHandler<String> {
      @Override
      protected void setNonNullParameter(PreparedStatement ps, int i, String parameter, JdbcType jdbcType) throws SQLException {
          ps.setString(i, parameter);
      }
  }
  ```

- 通过记录入参的类型，反射将值加入value中，并使用getTypeHandler();得到typeHandler的实现类

- ```java
  public void setParameters(PreparedStatement ps) throws SQLException {
      List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
      if (null != parameterMappings) {
          for (int i = 0; i < parameterMappings.size(); i++) {
              ParameterMapping parameterMapping = parameterMappings.get(i);
              String propertyName = parameterMapping.getProperty();
              Object value;
              if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
                  value = parameterObject;
              } else {
                  // 通过 MetaObject.getValue 反射取得值设进去
                  MetaObject metaObject = configuration.newMetaObject(parameterObject);
                  value = metaObject.getValue(propertyName);
              }
              JdbcType jdbcType = parameterMapping.getJdbcType();
              // 设置参数
              logger.info("根据每个ParameterMapping中的TypeHandler设置对应的参数信息 value：{}", JSON.toJSONString(value));
              TypeHandler typeHandler = parameterMapping.getTypeHandler();
              typeHandler.setParameter(ps, i + 1, value, jdbcType);
          }
      }
  }
  ```