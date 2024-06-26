package com.pyip.mybatis;

import java.sql.Connection;
import java.util.Map;

/**例
 * <mapper namespace="com.pyip.mybatis.test.dao.IUserDao">
 *     <select id="queryUserInfoById" parameterType="java.lang.Long"
 *          resultType="com.pyip.mybatis.test.po.User">
 *         SELECT id, userId, userName, userHead, createTime
 *         FROM user
 *         where id = #{id}
 *     </select>
 * </mapper>
 */
public class XNode {
    // 命名空间 com.pyip.mybatis.test.dao.IUserDao
    private String namespace;
    // id queryUserInfoById
    private String id;
    // 参数类型 java.lang.Long
    private String parameterType;
    // 返回类型 com.pyip.mybatis.test.po.User
    private String resultType;
    // sql语句
    // SELECT id, userId, userName, userHead, createTime FROM user where id = ?
    private String sql;
    // 参数填充位置及值 key:1,value:111333(id)
    private Map<Integer, String> parameter;

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getParameterType() {
        return parameterType;
    }

    public void setParameterType(String parameterType) {
        this.parameterType = parameterType;
    }

    public String getResultType() {
        return resultType;
    }

    public void setResultType(String resultType) {
        this.resultType = resultType;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public Map<Integer, String> getParameter() {
        return parameter;
    }

    public void setParameter(Map<Integer, String> parameter) {
        this.parameter = parameter;
    }
}
