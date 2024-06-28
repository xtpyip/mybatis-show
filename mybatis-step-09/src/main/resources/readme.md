### 本章总结

#### 问题分析

- 上一章使用了元对象反射工具包成功的将取值赋值等操作解除了耦合，提高了代码的可读性与可利用性，但也有如下的问题，在解析mapper数据时，所有的解析都在这一个方法中，耦合性太高，并且存在着硬编码问题，如何将这些粗粒度的XML解析器给细化，就是现有的问题

- ```java
  private void mapperElement(Element mappers) throws Exception {
      List<Element> mapperList = mappers.elements("mapper");
      for (Element e : mapperList) {
          String resource = e.attributeValue("resource");
          Reader reader = Resources.getResourceAsReader(resource);
          SAXReader saxReader = new SAXReader();
          Document document = saxReader.read(new InputSource(reader));
          Element root = document.getRootElement();
          //命名空间
          String namespace = root.attributeValue("namespace");
  
          // SELECT
          List<Element> selectNodes = root.elements("select");
          for (Element node : selectNodes) {
              String id = node.attributeValue("id");
              String parameterType = node.attributeValue("parameterType");
              String resultType = node.attributeValue("resultType");
              String sql = node.getText();
  
              // ? 匹配
              Map<Integer, String> parameter = new HashMap<>();
              Pattern pattern = Pattern.compile("(#\\{(.*?)})");
              Matcher matcher = pattern.matcher(sql);
              for (int i = 1; matcher.find(); i++) {
                  String g1 = matcher.group(1);
                  String g2 = matcher.group(2);
                  parameter.put(i, g2);
                  sql = sql.replace(g1, "?");
              }
  
              String msId = namespace + "." + id;
              String nodeName = node.getName();
              SqlCommandType sqlCommandType = SqlCommandType.valueOf(nodeName.toUpperCase(Locale.ENGLISH));
  
              BoundSql boundSql = new BoundSql(sql, parameter, parameterType, resultType);
  
              MappedStatement mappedStatement = new MappedStatement.Builder(configuration, msId, sqlCommandType, boundSql).build();
              // 添加解析 SQL
              configuration.addMappedStatement(mappedStatement);
          }
  
          // 注册Mapper映射器
          configuration.addMapper(Resources.classForName(namespace));
      }
  }
  ```

#### 本章重难点

##### 难点

- 首先，为了让代码的可读性更高，之前的类的属性有部分进行了修改，如下

- ```java
  // Configuration类
      protected Environment environment;//环境
      protected MapperRegistry mapperRegistry = new MapperRegistry(this);// 映射注册机
  	// 映射的语句，存在Map里
      protected final Map<String, MappedStatement> mappedStatements = new HashMap<>();
      // 类型别名注册机
      protected final TypeAliasRegistry typeAliasRegistry = new TypeAliasRegistry();
      // 语言驱动注册机
  	protected final LanguageDriverRegistry languageRegistry = new LanguageDriverRegistry();
      // 类型处理器注册机
      protected final TypeHandlerRegistry typeHandlerRegistry = new TypeHandlerRegistry();
      // 对象工厂和对象包装器工厂
      protected ObjectFactory objectFactory = new DefaultObjectFactory();
      protected ObjectWrapperFactory objectWrapperFactory = new DefaultObjectWrapperFactory();
  	// 已加载的资源文件（.xml文件）
      protected final Set<String> loadedResources = new HashSet<>();
  	// 数据库id
      protected String databaseId;
  // Environment 类
      private final String id;// 环境id
      private final TransactionFactory transactionFactory;// 事务工厂
      private final DataSource dataSource;// 数据源
  // MapperRegistry 类
      private Configuration config; // 配置文件
      // 将已添加的映射器代理加入到 HashMap
      private final Map<Class<?>, MapperProxyFactory<?>> knownMappers = new HashMap<>();
  // MappedStatement 类
  	private Configuration configuration;// 配置文件
      private String id; // 命名空间.id
      private SqlCommandType sqlCommandType; //枚举类UNKNOWN,INSERT,UPDATE,DELETE,SELECT;
      private SqlSource sqlSource; // sql相关内容
      Class<?> resultType; // 返回类型
  // SqlSource 接口
  // StaticSqlSource SqlSource实现类
      private String sql; // Sql语句
      private List<ParameterMapping> parameterMappings; // 相关sql信息
      private Configuration configuration; // 配置文件
  // ParameterMapping 类
  	private Configuration configuration;// 配置文件
      private String property;// property
      private Class<?> javaType = Object.class;// javaType = int
      private JdbcType jdbcType;// jdbcType=NUMERIC
  // TypeHandlerRegistry类
  	Map<JdbcType, TypeHandler<?>> JDBC_TYPE_HANDLER_MAP = new EnumMap<>(JdbcType.class);
  	// NUMERIC-（int-int处理器）
      Map<Type, Map<JdbcType, TypeHandler<?>>> TYPE_HANDLER_MAP = new HashMap<>();
      // int处理器Class - int处理器
  	Map<Class<?>, TypeHandler<?>> ALL_TYPE_HANDLERS_MAP = new HashMap<>();
  // BoundSql类 
      private String sql;// sql语句
      private List<ParameterMapping> parameterMappings; // sql中的类型参数映射
      private Object parameterObject;// 参数对象
      private Map<String, Object> additionalParameters;// 额外参数对象
      private MetaObject metaParameters; // Map类的反射器元对象
  
  
  ```

##### 重点

- 流程分析（公共部分）

  - 执行流程一致，都是先从配置文件解析到信息后，获取代理对象类，再通过代理对象类来执行方法

  - ```java
    // 1. 从SqlSessionFactory中获取SqlSession
    SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(Resources.getResourceAsReader("mybatis-config-datasource.xml"));
    SqlSession sqlSession = sqlSessionFactory.openSession();
    // 2. 获取映射器对象
    IUserDao userDao = sqlSession.getMapper(IUserDao.class);
    // 3. 测试验证
    User user = userDao.queryUserInfoById(1L);
    logger.info("测试结果：{}", JSON.toJSONString(user));
    ```

- 流程分析（修改部分）

  - 对所有的mapper标签对应的resource文件进行逐一解析

    - 

    ```java
    private void mapperElement(Element mappers) throws Exception {
        List<Element> mapperList = mappers.elements("mapper");
        for (Element e : mapperList) {
            String resource = e.attributeValue("resource");
            InputStream inputStream = Resources.getResourceAsStream(resource);
            // 在for循环里每个mapper都重新new一个XMLMapperBuilder，来解析
            XMLMapperBuilder mapperParser = new XMLMapperBuilder(inputStream, configuration, resource);
            mapperParser.parse();
        }
    }
    // mapperParser.parse()
        public void parse() throws Exception {
            // 如果当前资源没有加载过再加载，防止重复加载
            if (!configuration.isResourceLoaded(resource)) { // set集合
                configurationElement(element);
                // 标记一下，已经加载过了
                configuration.addLoadedResource(resource);
                // 绑定映射器到namespace
          configuration.addMapper(Resources.classForName(currentNamespace));
            }
        }
    // configurationElement(element);
        private void configurationElement(Element element) {
            // 1.配置namespace
            currentNamespace = element.attributeValue("namespace");
            if (currentNamespace.equals("")) {
                throw new RuntimeException("Mapper's namespace cannot be empty");
            }
            // 2.配置select|insert|update|delete
            buildStatementFromContext(element.elements("select"));
        }
    //  buildStatementFromContext(element.elements("select"));
    // 对指定标签进行解析存储
        private void buildStatementFromContext(List<Element> list) {
            for (Element element : list) {
                final XMLStatementBuilder statementParser = new XMLStatementBuilder(configuration, element, currentNamespace);
                statementParser.parseStatementNode();
            }
        }
    // statementParser.parseStatementNode();
        /*<select id="selectPerson" parameterType="int" parameterMap="deprecated" resultType="hashmap"  resultMap="personResultMap" flushCache="false"  useCache="true" timeout="10000"  fetchSize="256" statementType="PREPARED"  resultSetType="FORWARD_ONLY">
          SELECT * FROM PERSON WHERE ID = #{id}
        </select>*/
        public void parseStatementNode() {
            String id = element.attributeValue("id");
            // 参数类型
            String parameterType = element.attributeValue("parameterType");
            Class<?> parameterTypeClass = resolveAlias(parameterType);
            // 结果类型
            String resultType = element.attributeValue("resultType");
            Class<?> resultTypeClass = resolveAlias(resultType);
            // 获取命令类型(select|insert|update|delete)
            String nodeName = element.getName();
            // 获取执行类型，insert,update
            SqlCommandType sqlCommandType = SqlCommandType.valueOf(nodeName.toUpperCase(Locale.ENGLISH));
            // 获取默认语言驱动器
            Class<?> langClass = configuration.getLanguageRegistry().getDefaultDriverClass();
            LanguageDriver langDriver = configuration.getLanguageRegistry().getDriver(langClass);
            SqlSource sqlSource = langDriver.createSqlSource(configuration, element, parameterTypeClass);
            MappedStatement mappedStatement = new MappedStatement.Builder(configuration, currentNamespace + "." + id, sqlCommandType, sqlSource, resultTypeClass).build();
            // 添加解析 SQL
            configuration.addMappedStatement(mappedStatement);
        }
    
    ```

    - langDriver.createSqlSource(configuration, element, parameterTypeClass);

    - 

      ```java
      // XMLLanguageDriver implements LanguageDriver
      public SqlSource createSqlSource(Configuration configuration, Element script, Class<?> parameterType) {
          // 用XML脚本构建器解析
          XMLScriptBuilder builder = new XMLScriptBuilder(configuration, script, parameterType);
          return builder.parseScriptNode();
      }
      // builder.parseScriptNode();
          public SqlSource parseScriptNode() {
              List<SqlNode> contents = parseDynamicTags(element);
              MixedSqlNode rootSqlNode = new MixedSqlNode(contents);
              return new RawSqlSource(configuration, rootSqlNode, parameterType);
          }
      // parseDynamicTags(element);
          List<SqlNode> parseDynamicTags(Element element) {
              List<SqlNode> contents = new ArrayList<>();
              // element.getText 拿到 SQL
              String data = element.getText();
              contents.add(new StaticTextSqlNode(data));
              return contents;
          }
      // new RawSqlSource(configuration, rootSqlNode, parameterType);
      // 返回SqlSource的实现类
      ```

    - MappedStatement mappedStatement = new MappedStatement.Builder(configuration, currentNamespace + "." + id, sqlCommandType, sqlSource, resultTypeClass).build();
      - 添加解析sql
      - 放入Configuration中的mappedStatements，命名空间.id-sql相关信息

  - 执行方法（更改部分）

    - 原与修改，BoundSql类的取用过程

    - ```java
      public <T> T selectOne(String statement, Object parameter) {
          MappedStatement ms = configuration.getMappedStatement(statement);
          List<T> list = executor.query(ms, parameter, Executor.NO_RESULT_HANDLER, ms.getBoundSql());
          return list.get(0);
      }
      ```

    - ```java
      public <T> T selectOne(String statement, Object parameter) {
          MappedStatement ms = configuration.getMappedStatement(statement);
          List<T> list = executor.query(ms, parameter, Executor.NO_RESULT_HANDLER, ms.getSqlSource().getBoundSql(parameter));
          return list.get(0);
      }
      ```