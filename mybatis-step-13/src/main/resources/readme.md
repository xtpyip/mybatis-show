### 本章总结

#### 本章小结

- 流程分析

- ```java
  // 如果配置文件中开启了class，<mapper class="com.pyip.mybatis.test.dao.IUserDao"/>
  // 则使用注解解析
  String resource = e.attributeValue("resource");
  String mapperClass = e.attributeValue("class");
  // XML 解析
  if (resource != null && mapperClass == null) {
      InputStream inputStream = Resources.getResourceAsStream(resource);
      // 在for循环里每个mapper都重新new一个XMLMapperBuilder，来解析
      XMLMapperBuilder mapperParser = new XMLMapperBuilder(inputStream, configuration, resource);
      mapperParser.parse();
  }
  // Annotation 注解解析
  else if (resource == null && mapperClass != null) {
      Class<?> mapperInterface = Resources.classForName(mapperClass);
      configuration.addMapper(mapperInterface);
  }
  ```

- 对接口的所有方法进行处理

- ```java
  public void parse() {
      String resource = type.toString();
      if (!configuration.isResourceLoaded(resource)) {
          assistant.setCurrentNamespace(type.getName());
  
          Method[] methods = type.getMethods();
          for (Method method : methods) {
              if (!method.isBridge()) {
                  // 解析语句
                  parseStatement(method);
              }
          }
      }
  }
  
  //
  private void parseStatement(Method method) {
          Class<?> parameterTypeClass = getParameterType(method);
          LanguageDriver languageDriver = getLanguageDriver(method);
          SqlSource sqlSource = getSqlSourceFromAnnotations(method, parameterTypeClass, languageDriver);
      // 是有注解的sql语句
          if (sqlSource != null) {
              final String mappedStatementId = type.getName() + "." + method.getName();
              SqlCommandType sqlCommandType = getSqlCommandType(method);
              boolean isSelect = sqlCommandType == SqlCommandType.SELECT;
              String resultMapId = null;
              if (isSelect) {
                  resultMapId = parseResultMap(method);
              }
              // 调用助手类
              assistant.addMappedStatement(
                      mappedStatementId,
                      sqlSource,
                      sqlCommandType,
                      parameterTypeClass,
                      resultMapId,
                      getReturnType(method),
                      languageDriver
              );
          }
      }
  //
       private String parseResultMap(Method method) {
          // generateResultMapName
          StringBuilder suffix = new StringBuilder();
          for (Class<?> c : method.getParameterTypes()) {
              suffix.append("-");
              suffix.append(c.getSimpleName());
          }
          if (suffix.length() < 1) {
              suffix.append("-void");
          }
          String resultMapId = type.getName() + "." + method.getName() + suffix;
  
          // 添加 ResultMap
          Class<?> returnType = getReturnType(method);
          assistant.addResultMap(resultMapId, returnType, new ArrayList<>());
          return resultMapId;
      }
  //
      public MappedStatement addMappedStatement(
              String id,
              SqlSource sqlSource,
              SqlCommandType sqlCommandType,
              Class<?> parameterType,
              String resultMap,
              Class<?> resultType,
              LanguageDriver lang
      ) {
          // 给id加上namespace前缀：com.pyip.mybatis.test.dao.IUserDao.queryUserInfoById
          id = applyCurrentNamespace(id, false);
          MappedStatement.Builder statementBuilder = new MappedStatement.Builder(configuration, id, sqlCommandType, sqlSource, resultType);
          // 结果映射，给 MappedStatement#resultMaps
          setStatementResultMap(resultMap, resultType, statementBuilder);
          MappedStatement statement = statementBuilder.build();
          // 映射语句信息，建造完存放到配置项中
          configuration.addMappedStatement(statement);
          return statement;
      }
  ```

- 调用方法，即是查询namespace.id的sql相关信息，来执行sql相关信息，后继与xml配置一致。