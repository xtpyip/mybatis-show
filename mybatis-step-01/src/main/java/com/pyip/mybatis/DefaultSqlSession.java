package com.pyip.mybatis;

import java.util.Date;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 简化ORM框架的处理流程
 * 包装了元素的提取、数据库的连接、jdbc的执行、并且完成了SQL语句执行时入参、出参的处理
 * 最终返回查询结果
 */
public class DefaultSqlSession implements SqlSession{
    private Connection connection;
    private Map<String,XNode> mapperElement;

    // 构造器，传入相应的连接与读取的配置文件信息
    public DefaultSqlSession(Connection connection,Map<String,XNode> mapperElement){
        this.connection = connection;
        this.mapperElement = mapperElement;
    }
    @Override
    public <T> T selectOne(String statement) {
        try{
            // 根据statement(命令空间key)获取XNode信息
            XNode xNode = mapperElement.get(statement);
            // 通过xNode内的sql语句来构建预处理声明
            PreparedStatement preparedStatement = connection.prepareStatement(xNode.getSql());
            // 执行sql语句，返回结果集
            ResultSet resultSet = preparedStatement.executeQuery();
            // 通过反射，利用xNode的结果类型，将set结果封装到objects中
            List<T> objects = resultSet2Obj(resultSet,Class.forName(xNode.getResultType()));
            return objects.get(0); // 返回第一个元素，可能有异常，有try-catch;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    // statement：com.pyip.mybatis.test.dao.IUserDao.queryUserInfoById
    @Override
    public <T> T selectOne(String statement, Object parameter) {
        // 获取XNode信息
        XNode xNode = mapperElement.get(statement);
        // 通过xNode获取参数位置及参数值
        Map<Integer, String> parameterMap = xNode.getParameter();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(xNode.getSql());
            buildParameter(preparedStatement, parameter, parameterMap);
            ResultSet resultSet = preparedStatement.executeQuery();
            List<T> objects = resultSet2Obj(resultSet, Class.forName(xNode.getResultType()));
            return objects.get(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public <T> List<T> selectList(String statement) {
        try{
            // 获取XNode信息
            XNode xNode = mapperElement.get(statement);
            // 通过xNode内的sql语句来构建预处理声明
            PreparedStatement preparedStatement = connection.prepareStatement(xNode.getSql());
            // 执行sql语句，返回结果集
            ResultSet resultSet = preparedStatement.executeQuery();
            // 通过反射，利用xNode的结果类型，将set结果封装起来返回
            return resultSet2Obj(resultSet,Class.forName(xNode.getResultType()));
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public <T> List<T> selectList(String statement, Object parameter) {
        XNode xNode = mapperElement.get(statement);
        Map<Integer, String> parameterMap = xNode.getParameter();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(xNode.getSql());
            buildParameter(preparedStatement, parameter, parameterMap);
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet2Obj(resultSet, Class.forName(xNode.getResultType()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // 包装了日常使用JDBC操作数据库执行SQL语句，并对返回的数据进行处理的逻辑
    public <T> List<T> resultSet2Obj(ResultSet resultSet,Class<?> clazz){
        List<T> list = new ArrayList<>();
        try{
            // 获取set的元数据
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            // 遍历所有的行值
            while (resultSet.next()) {
                T obj = (T) clazz.newInstance();// 强转为指定类，实例化clazz
                // 遍历所有的列
                for (int i = 1; i <= columnCount; i++) {
                    Object value = resultSet.getObject(i);
                    String columnName = metaData.getColumnName(i); // 获取数据库的列名，如user
                    String setMethod = "set" + columnName.substring(0,1).toUpperCase() + columnName.substring(1); // 得到set指定类的方法名，如setUser()
                    Method method;
                    if(value instanceof Timestamp){
                        // 把时间戳转为Date日期类型
                        // getMethod方法则根据方法名称和相关参数，来定位需要查找的Method对象并返回。
                        method = clazz.getMethod(setMethod,Date.class);
                    }else{
                        // 将
                        method = clazz.getMethod(setMethod,value.getClass());
                    }
                    method.invoke(obj,value); // 反射，obj执行method方法，将value设置入obj中
                }
                list.add(obj);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public void close() {
        if(null == connection) return;
        try {
            connection.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * @param preparedStatement 预处理语句
     * @param parameter 参数
     * @param parameterMap 位置与xxx
     */
    private void buildParameter(PreparedStatement preparedStatement, Object parameter,
                                Map<Integer, String> parameterMap) throws SQLException, IllegalAccessException {
        int size = parameterMap.size();
        // 单个参数
        if(parameter instanceof java.lang.Long){
            for (int i = 1; i <= size; i++) {
                // 设置？的值 select * from user where id = ?
                preparedStatement.setLong(i, Long.parseLong(parameter.toString()));
            }
            return;
        }
        if (parameter instanceof Integer) {
            for (int i = 1; i <= size; i++) {
                preparedStatement.setInt(i, Integer.parseInt(parameter.toString()));
            }
            return;
        }
        if (parameter instanceof String) {
            for (int i = 1; i <= size; i++) {
                preparedStatement.setString(i, parameter.toString());
            }
            return;
        }
        Map<String, Object> fieldMap = new HashMap<>();
        // 对象参数
        Field[] declaredFields = parameter.getClass().getDeclaredFields();
        for (Field field : declaredFields) {
            String name = field.getName();
            field.setAccessible(true);
            Object obj = field.get(parameter);
            field.setAccessible(true);
            fieldMap.put(name,obj);
        }
        for (int i = 1; i <= size; i++) {
            String parameterDefine = parameterMap.get(i);
            Object obj = fieldMap.get(parameterDefine);

            if(obj instanceof Short){
                preparedStatement.setShort(i,Short.parseShort(obj.toString()));
                continue;
            }

            if(obj instanceof Integer){
                preparedStatement.setInt(i,Integer.parseInt(obj.toString()));
                continue;
            }
            if(obj instanceof Long){
                preparedStatement.setLong(i,Long.parseLong(obj.toString()));
                continue;
            }
            if(obj instanceof String){
                preparedStatement.setString(i,obj.toString());
                continue;
            }
            if(obj instanceof java.util.Date){
                preparedStatement.setDate(i,(java.sql.Date)obj);
            }
        }
    }
}
