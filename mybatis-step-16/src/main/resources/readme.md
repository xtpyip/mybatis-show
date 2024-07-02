### 本章总结

#### 本章难点

- 解析动态sql语句，如

- ```xml
  <select id="queryActivityById" parameterType="com.pyip.mybatis.test.po.Activity" resultMap="activityMap">
      SELECT activity_id, activity_name, activity_desc, create_time, update_time
      FROM activity
      <trim prefix="WHERE" prefixOverrides="AND | OR" suffixOverrides="and">
          <if test="null != activityId">
              activity_id = #{activityId}
          </if>
      </trim>
  </select>
  ```

#### 流程分析

- 使用了设计模式中的组合模式，其对目录类的工程处理级其方便
- ![组合模式处理树状结构](img/example.png)![xml解析节点类](img\sqlNode.png)
- 使用ognl对表达式进行判断，是否添加其内部的值

- 分析

- ```java
  // 对所有的xml的tag进行封装成SqlNode
  public SqlSource parseScriptNode() {
      List<SqlNode> contents = parseDynamicTags(element);
      MixedSqlNode rootSqlNode = new MixedSqlNode(contents);
      SqlSource sqlSource = null;
      if (isDynamic) {
          sqlSource = new DynamicSqlSource(configuration, rootSqlNode);
      } else {
          sqlSource = new RawSqlSource(configuration, rootSqlNode, parameterType);
      }
      return sqlSource;
  }
  
  //
      List<SqlNode> parseDynamicTags(Element element) {
          List<SqlNode> contents = new ArrayList<>();
          List<Node> children = element.content();
          for (Node child : children) {
              if (child.getNodeType() == Node.TEXT_NODE || child.getNodeType() == Node.CDATA_SECTION_NODE) {
                  String data = child.getText();
                  TextSqlNode textSqlNode = new TextSqlNode(data);
                  if (textSqlNode.isDynamic()) {
                      contents.add(textSqlNode);
                      isDynamic = true;
                  } else {
                      contents.add(new StaticTextSqlNode(data));
                  }
              } else if (child.getNodeType() == Node.ELEMENT_NODE) {
                  String nodeName = child.getName();
                  NodeHandler handler = nodeHandlerMap.get(nodeName);
                  if (handler == null) {
                      throw new RuntimeException("Unknown element <" + nodeName + "> in SQL statement.");
                  }
                  handler.handleNode(element.element(child.getName()), contents);
                  isDynamic = true;
              }
          }
          return contents;
      }
  //     private class IfHandler implements NodeHandler {
          @Override
          public void handleNode(Element nodeToHandle, List<SqlNode> targetContents) {
              List<SqlNode> contents = parseDynamicTags(nodeToHandle);
              MixedSqlNode mixedSqlNode = new MixedSqlNode(contents);
              String test = nodeToHandle.attributeValue("test");
              IfSqlNode ifSqlNode = new IfSqlNode(mixedSqlNode, test);
              targetContents.add(ifSqlNode);
          }
      }
  ```

- 如同递归一样，对每一层的所有子节点都进行存储，如IfSqlNode,TrimSqlNode,trim/where/set/foreach/if/choose/when/otherwise/bind等等

- 在执行调用语句时，进行ognl判断拼接

- ```java
  @Override
  public <E> List<E> selectList(String statement, Object parameter) {
      logger.info("执行查询 statement：{} parameter：{}", statement, JSON.toJSONString(parameter));
      MappedStatement ms = configuration.getMappedStatement(statement);
          return executor.query(ms, parameter, RowBounds.DEFAULT, Executor.NO_RESULT_HANDLER, ms.getSqlSource().getBoundSql(parameter));
  }
  // ms.getSqlSource().getBoundSql(parameter)
      public BoundSql getBoundSql(Object parameterObject) {
          // 生成一个 DynamicContext 动态上下文
          DynamicContext context = new DynamicContext(configuration, parameterObject);
          // SqlNode.apply 将 ${} 参数替换掉，不替换 #{} 这种参数
          rootSqlNode.apply(context);
          // 调用 SqlSourceBuilder
          SqlSourceBuilder sqlSourceParser = new SqlSourceBuilder(configuration);
          Class<?> parameterType = parameterObject == null ? Object.class : parameterObject.getClass();
          // SqlSourceBuilder.parse 这里返回的是 StaticSqlSource，解析过程就把那些参数都替换成?了，也就是最基本的JDBC的SQL语句。
          SqlSource sqlSource = sqlSourceParser.parse(context.getSql(), parameterType, context.getBindings());
          // SqlSource.getBoundSql，非递归调用，而是调用 StaticSqlSource 实现类
          BoundSql boundSql = sqlSource.getBoundSql(parameterObject);
          for (Map.Entry<String, Object> entry : context.getBindings().entrySet()) {
              boundSql.setAdditionalParameter(entry.getKey(), entry.getValue());
          }
          return boundSql;
      }
  
  // rootSqlNode.apply(context);
      public boolean apply(DynamicContext context) {
          // 依次调用list里每个元素的apply
          contents.forEach(node -> node.apply(context));
          return true;
      }
  ```

- 将所有的trim等处理后的信息存储到context中去，最后通过context.getSql()获取拼接后的可执行sql语句