### 数据源池化技术

- 无池化连接

  - 正常的创建连接，过程和JDBC，DRUID一样

  - ```
    UnpooledDataSourceFactory implements DataSourceFactory
    ```

- 有池化连接

  - 类型别名注册器已经注册了所有的数据源对应的工厂类

  - ```java
    typeAliasRegistry.registerAlias("JDBC", JdbcTransactionFactory.class);
    typeAliasRegistry.registerAlias("DRUID", DruidDataSourceFactory.class);
    typeAliasRegistry.registerAlias("UNPOOLED", UnpooledDataSourceFactory.class);
    typeAliasRegistry.registerAlias("POOLED", PooledDataSourceFactory.class);
    ```

  - 有池化连接获取连接后关闭，是可以取到同一个的（这里实现的从连接池中取连接只取第一个）

  - 实现比较复杂，这里简述即可