### 本章总结

#### 问题分析

- 上一章我们把sql语句的执行相关下放到了下面的executor执行器去，但在以往的过程中我们发现，在创建Environment环境类时，都要使用到以下代码

- ```java
  // XMLConfigBuilder
  // 对<datasourcee>标签进行解析时
  dataSourceFactory.setProperties(props);
  // DataSourceFactory 接口实现类UnPooledDataSourceFactory
      protected Properties props;
      @Override
      public void setProperties(Properties props) {
          this.props = props;
      }
      public DataSource getDataSource() {
          UnpooledDataSource unpooledDataSource = new UnpooledDataSource();
          unpooledDataSource.setDriver(props.getProperty("driver"));
          unpooledDataSource.setUrl(props.getProperty("url"));
          unpooledDataSource.setUsername(props.getProperty("username"));
          unpooledDataSource.setPassword(props.getProperty("password"));
          return unpooledDataSource;
      }
  ```

- 这些代码存在硬耦合问题，对程序的扩展性有极大的影响

#### 本章小节

- 使用反射创建类的反射类，使用反射类对类进行相应的数据填充等等

- 使用反射技术把props中的driver,url,name,password信息加入dataSource中

- ```java
  // DataSourceFactory实现类 UnpooledDataSourceFactory
  protected DataSource dataSource;
  public UnpooledDataSourceFactory() {
      this.dataSource = new UnpooledDataSource();
  }
  
  @Override
  public void setProperties(Properties props) {
      MetaObject metaObject = SystemMetaObject.forObject(dataSource);
      for (Object key : props.keySet()) { // 遍历所有的props
          String propertyName = (String) key;
          if (metaObject.hasSetter(propertyName)) {
              String value = (String) props.get(propertyName);
              Object convertedValue = convertValue(metaObject, propertyName, value);
              metaObject.setValue(propertyName, convertedValue); // 执行对应的SetField(name,value)方法
          }
      }
  }
  ```

- MetaObject metaObject = SystemMetaObject.forObject(dataSource);方法解析，返回dataSource类的MetaObject,并为dataSource赋值，其余步骤与上章一致

- ```java
  // SystemMetaObject object为DataSource
  public static MetaObject forObject(Object object) {
      return MetaObject.forObject(object, DEFAULT_OBJECT_FACTORY, DEFAULT_OBJECT_WRAPPER_FACTORY);
  }
  // MetaObject
      public static MetaObject forObject(Object object, ObjectFactory objectFactory, ObjectWrapperFactory objectWrapperFactory) {
          if (object == null) {
              // 处理一下null,将null包装起来
              return SystemMetaObject.NULL_META_OBJECT;
          } else {
              return new MetaObject(object, objectFactory, objectWrapperFactory);
          }
      }
  // MetaObject 构造方法
  		if (object instanceof ObjectWrapper) {
              // 如果对象本身已经是ObjectWrapper型，则直接赋给objectWrapper
              this.objectWrapper = (ObjectWrapper) object;
          } else if (objectWrapperFactory.hasWrapperFor(object)) {
              // 如果有包装器,调用ObjectWrapperFactory.getWrapperFor
              this.objectWrapper = objectWrapperFactory.getWrapperFor(this, object);
          } else if (object instanceof Map) {
              // 如果是Map型，返回MapWrapper
              this.objectWrapper = new MapWrapper(this, (Map) object);
          } else if (object instanceof Collection) {
              // 如果是Collection型，返回CollectionWrapper
              this.objectWrapper = new CollectionWrapper(this, (Collection) object);
          } else {
              // 除此以外，返回BeanWrapper
              this.objectWrapper = new BeanWrapper(this, object);
          }
  // BeanWrapper 继承BaseWrapper
      public BeanWrapper(MetaObject metaObject, Object object) {
          // beanWrapper有三个属性MetaObject,Object,MetaClass
          super(metaObject);
          this.object = object;
          this.metaClass = MetaClass.forClass(object.getClass());// object的反射类
      }
  
  ```