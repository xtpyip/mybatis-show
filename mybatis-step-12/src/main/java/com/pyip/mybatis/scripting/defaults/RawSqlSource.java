package com.pyip.mybatis.scripting.defaults;

import com.pyip.mybatis.builder.SqlSourceBuilder;
import com.pyip.mybatis.mapping.BoundSql;
import com.pyip.mybatis.mapping.SqlSource;
import com.pyip.mybatis.scripting.xmltags.DynamicContext;
import com.pyip.mybatis.scripting.xmltags.SqlNode;
import com.pyip.mybatis.session.Configuration;

import java.util.HashMap;

/**
 * @author 小傅哥，微信：fustack
 * @description 原始SQL源码，比 DynamicSqlSource 动态SQL处理快
 * @github https://github.com/fuzhengwei
 * @Copyright 公众号：bugstack虫洞栈 | 博客：https://bugstack.cn - 沉淀、分享、成长，让自己和他人都能有所收获！
 */
public class RawSqlSource implements SqlSource {

    private final SqlSource sqlSource;

    public RawSqlSource(Configuration configuration, SqlNode rootSqlNode, Class<?> parameterType) {
        this(configuration, getSql(configuration, rootSqlNode), parameterType);
    }

    public RawSqlSource(Configuration configuration, String sql, Class<?> parameterType) {
        SqlSourceBuilder sqlSourceParser = new SqlSourceBuilder(configuration);
        Class<?> clazz = parameterType == null ? Object.class : parameterType;
        sqlSource = sqlSourceParser.parse(sql, clazz, new HashMap<>());
    }

    @Override
    public BoundSql getBoundSql(Object parameterObject) {
        return sqlSource.getBoundSql(parameterObject);
    }

    private static String getSql(Configuration configuration, SqlNode rootSqlNode) {
        DynamicContext context = new DynamicContext(configuration, null);
        rootSqlNode.apply(context);
        return context.getSql();
    }

}
