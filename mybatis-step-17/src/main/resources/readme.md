### 本章总结
#### 本章难点
- 插件功能是如何实现的，并怎么在某个方法执行开始，结束或某个时刻调用
#### 实现流程
- 首先要先定义出插件的位置及值

- ```xml
  <plugins>
      <plugin interceptor="com.pyip.mybatis.test.plugin.TestPlugin">
          <property name="test00" value="100"/>
          <property name="test01" value="200"/>
      </plugin>
  </plugins>
  ```

- 解析xml并存储入Configuration类中

- ```java
  private void pluginElement(Element parent) throws Exception {
      if (parent == null) return;
      List<Element> elements = parent.elements();
      for (Element element : elements) {
          String interceptor = element.attributeValue("interceptor");
          // 参数配置
          Properties properties = new Properties();
          List<Element> propertyElementList = element.elements("property");
          for (Element property : propertyElementList) {
              properties.setProperty(property.attributeValue("name"), property.attributeValue("value"));
          }
          // 获取插件实现类并实例化：cn.bugstack.mybatis.test.plugin.TestPlugin
          Interceptor interceptorInstance = (Interceptor) resolveClass(interceptor).newInstance();
          interceptorInstance.setProperties(properties);
          configuration.addInterceptor(interceptorInstance);
      }
  }
  
  //指明在哪此类，的哪些方法执行时，调用插件
  @Intercepts({@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class})})
  public class TestPlugin implements Interceptor {
  
      @Override
      public Object intercept(Invocation invocation) throws Throwable {
          // 获取StatementHandler
          StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
          // 获取SQL信息
          BoundSql boundSql = statementHandler.getBoundSql();
          String sql = boundSql.getSql();
          // 输出SQL
          System.out.println("拦截SQL：" + sql);
          // 放行
          return invocation.proceed();
      }
  
      @Override
      public void setProperties(Properties properties) {
          System.out.println("参数输出：" + properties.getProperty("test00"));
      }
  
  }
  ```

- ![](img\plugin.png)
- ![](img\invocation.png)

- 自定义插件相关类

- ```java
  public interface Interceptor {
      // 拦截，使用方实现
      Object intercept(Invocation invocation) throws Throwable;
      // 代理
      default Object plugin(Object target) {
          return Plugin.wrap(target, this);
      }
      // 设置属性
      default void setProperties(Properties properties) {
          // NOP
      }
  }
  // 拦截器链
  public class InterceptorChain {
      private final List<Interceptor> interceptors = new ArrayList<>();
      public Object pluginAll(Object target) {
          for (Interceptor interceptor : interceptors) {
              target = interceptor.plugin(target);
          }
          return target;
      }
      public void addInterceptor(Interceptor interceptor) {
          interceptors.add(interceptor);
      }
      public List<Interceptor> getInterceptors() {
          return Collections.unmodifiableList(interceptors);
      }
  }
  // 拦截注解
  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.TYPE)
  public @interface Intercepts {
      Signature[] value();
  }
  // 方法签名
  public @interface Signature {
      // 被拦截类
      Class<?> type();
      // 被拦截类的方法
      String method();
      // 被拦截类的方法的参数
      Class<?>[] args();
  }
  
  // 调用信息
  public class Invocation {
      // 调用的对象
      private Object target;
      // 调用的方法
      private Method method;
      // 调用的参数
      private Object[] args;
      public Invocation(Object target, Method method, Object[] args) {
          this.target = target;
          this.method = method;
          this.args = args;
      }
      public Object getTarget() {
          return target;
      }
      public Method getMethod() {
          return method;
      }
      public Object[] getArgs() {
          return args;
      }
      // 放行；调用执行
      public Object proceed() throws InvocationTargetException, IllegalAccessException {
          return method.invoke(target, args);
      }
  }
  
  ```

- 在处理声明时嵌入插件

- ```java
  // Configuration
  public StatementHandler newStatementHandler(Executor executor, MappedStatement mappedStatement, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) {
      // 创建语句处理器，Mybatis 这里加了路由 STATEMENT、PREPARED、CALLABLE 我们默认只根据预处理进行实例化
      StatementHandler statementHandler = new PreparedStatementHandler(executor, mappedStatement, parameter, rowBounds, resultHandler, boundSql);
      // 嵌入插件，代理对象
      statementHandler = (StatementHandler) interceptorChain.pluginAll(statementHandler);
      return statementHandler;
  }
  // interceptorChain.pluginAll(statementHandler)
      public Object pluginAll(Object target) {
          for (Interceptor interceptor : interceptors) {
              target = interceptor.plugin(target);
          }
          return target;
      }
  // interceptor.plugin(target);
      // 代理
      default Object plugin(Object target) {
          return Plugin.wrap(target, this);
      }
  //用代理把自定义插件行为包裹到目标方法中，也就是 Plugin.invoke 的过滤调用
      public static Object wrap(Object target, Interceptor interceptor) {
          // 取得签名Map
          Map<Class<?>, Set<Method>> signatureMap = getSignatureMap(interceptor);
          // 取得要改变行为的类(ParameterHandler|ResultSetHandler|StatementHandler|Executor)，目前只添加了 StatementHandler
          Class<?> type = target.getClass();
          // 取得接口
          Class<?>[] interfaces = getAllInterfaces(type, signatureMap);
          // 创建代理(StatementHandler)
          if (interfaces.length > 0) {
              // Proxy.newProxyInstance(ClassLoader loader, Class<?>[] interfaces, InvocationHandler h)
              return Proxy.newProxyInstance(
                      type.getClassLoader(),
                      interfaces,
                      new Plugin(target, interceptor, signatureMap));
          }
          return target;
      }
  ```

- 当执行到StatementHandler的prepare方法时，调用

- ```java
  public Object intercept(Invocation invocation) throws Throwable {
      // 获取StatementHandler
      StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
      // 获取SQL信息
      BoundSql boundSql = statementHandler.getBoundSql();
      String sql = boundSql.getSql();
      // 输出SQL
      System.out.println("拦截SQL：" + sql);
      // 放行
      return invocation.proceed();
  }
  // 放行；调用执行
      public Object proceed() throws InvocationTargetException, IllegalAccessException {
          return method.invoke(target, args);
      }
  ```

- 继续执行prepare方法，后续一样。