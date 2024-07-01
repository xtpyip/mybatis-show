### 本章总结

#### 问题分析

- java的字段与mysql的字段是不相对应的，如何将字段与字段对应问题

- ```xml
  <resultMap id="activityMap" type="com.pyip.mybatis.test.po.Activity">
      <id column="id" property="id"/>
      <result column="activity_id" property="activityId"/>
      <result column="activity_name" property="activityName"/>
      <result column="activity_desc" property="activityDesc"/>
      <result column="create_time" property="createTime"/>
      <result column="update_time" property="updateTime"/>
  </resultMap>
  ```

#### 本章小结

- 流程分析

  - 解析xml文件，将所有的resultMap映射规则标签记录在Configuration中去

  - ```xml
    <select id="queryActivityById" parameterType="java.lang.Long" resultMap="activityMap">
        SELECT activity_id, activity_name, activity_desc, create_time, update_time
        FROM activity
        WHERE activity_id = #{activityId}
    </select>
    ```

  - 对于任何使用到了这个标签的查询语句，进行反射值映射填充

  - ```java
    // 获取一行的值
    private Object getRowValue(ResultSetWrapper rsw, ResultMap resultMap) throws SQLException {
        // 根据返回类型，实例化对象
        Object resultObject = createResultObject(rsw, resultMap, null);
        if (resultObject != null && !typeHandlerRegistry.hasTypeHandler(resultMap.getType())) {
            final MetaObject metaObject = configuration.newMetaObject(resultObject);
            // 自动映射：把每列的值都赋到对应的字段上
            applyAutomaticMappings(rsw, resultMap, metaObject, null);
            // Map映射：根据映射类型赋值到字段
            applyPropertyMappings(rsw, resultMap, metaObject, null);
        }
        return resultObject;
    }
    
    private boolean applyAutomaticMappings(ResultSetWrapper rsw, ResultMap resultMap, MetaObject metaObject, String columnPrefix) throws SQLException {
            final List<String> unmappedColumnNames = rsw.getUnmappedColumnNames(resultMap, columnPrefix);
            boolean foundValues = false;
            for (String columnName : unmappedColumnNames) {
                String propertyName = columnName;
                if (columnPrefix != null && !columnPrefix.isEmpty()) {
                    // When columnPrefix is specified,ignore columns without the prefix.
                    if (columnName.toUpperCase(Locale.ENGLISH).startsWith(columnPrefix)) {
                        propertyName = columnName.substring(columnPrefix.length());
                    } else {
                        continue;
                    }
                }
                final String property = metaObject.findProperty(propertyName, false);
                if (property != null && metaObject.hasSetter(property)) {
                    final Class<?> propertyType = metaObject.getSetterType(property);
                    if (typeHandlerRegistry.hasTypeHandler(propertyType)) {
                        final TypeHandler<?> typeHandler = rsw.getTypeHandler(propertyType, columnName);
                        // 使用 TypeHandler 取得结果
                        final Object value = typeHandler.getResult(rsw.getResultSet(), columnName);
                        if (value != null) {
                            foundValues = true;
                        }
                        if (value != null || !propertyType.isPrimitive()) {
                            // 通过反射工具类设置属性值
                            metaObject.setValue(property, value);
                        }
                    }
                }
            }
            return foundValues;
        }
    ```

  - 使用反射，和column与property对应的map，将结果封装起来

  - ```xml
    private boolean applyPropertyMappings(ResultSetWrapper rsw, ResultMap resultMap, MetaObject metaObject, String columnPrefix) throws SQLException {
        final List<String> mappedColumnNames = rsw.getMappedColumnNames(resultMap, columnPrefix);
        boolean foundValues = false;
        final List<ResultMapping> propertyMappings = resultMap.getPropertyResultMappings();
        for (ResultMapping propertyMapping : propertyMappings) {
            final String column = propertyMapping.getColumn();
            if (column != null && mappedColumnNames.contains(column.toUpperCase(Locale.ENGLISH))) {
                // 获取值
                final TypeHandler<?> typeHandler = propertyMapping.getTypeHandler();
                Object value = typeHandler.getResult(rsw.getResultSet(), column);
                // 设置值
                final String property = propertyMapping.getProperty();
                if (value != NO_VALUE && property != null && value != null) {
                    // 通过反射工具类设置属性值
                    metaObject.setValue(property, value);
                    foundValues = true;
                }
            }
        }
        return foundValues;
    }
    ```

- 对结果进行返回