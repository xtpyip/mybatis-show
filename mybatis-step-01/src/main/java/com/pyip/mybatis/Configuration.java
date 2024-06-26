package com.pyip.mybatis;

import java.sql.Connection;
import java.util.Map;

public class Configuration {
    // 数据库连接
    protected Connection connection;
    // 数据库信息（driver,url,username,password
    protected Map<String, String> dataSource;
    // key:namespace+"."+id
    // value: xNode值 存储着 namespace,id,参数类型，返回类型，sql语句，有参
    //                    xNode.setNamespace(namespace);
    //                    xNode.setId(id);
    //                    xNode.setParameterType(parameterType);
    //                    xNode.setResultType(resultType);
    //                    xNode.setSql(sql);
    //                    xNode.setParameter(parameter);
    protected Map<String, XNode> mapperElement;

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public void setDataSource(Map<String, String> dataSource) {
        this.dataSource = dataSource;
    }

    public void setMapperElement(Map<String, XNode> mapperElement) {
        this.mapperElement = mapperElement;
    }
}
