package com.pyip.mybatis.scripting.xmltags;

import com.pyip.mybatis.executor.parameter.ParameterHandler;
import com.pyip.mybatis.mapping.BoundSql;
import com.pyip.mybatis.mapping.MappedStatement;
import com.pyip.mybatis.mapping.SqlSource;
import com.pyip.mybatis.scripting.LanguageDriver;
import com.pyip.mybatis.scripting.defaults.DefaultParameterHandler;
import com.pyip.mybatis.scripting.defaults.RawSqlSource;
import com.pyip.mybatis.session.Configuration;
import org.dom4j.Element;

/**
 * @author 小傅哥，微信：fustack
 * @description XML语言驱动器
 * @github https://github.com/fuzhengwei
 * @Copyright 公众号：bugstack虫洞栈 | 博客：https://bugstack.cn - 沉淀、分享、成长，让自己和他人都能有所收获！
 */
public class XMLLanguageDriver implements LanguageDriver {

    @Override
    public SqlSource createSqlSource(Configuration configuration, Element script, Class<?> parameterType) {
        // 用XML脚本构建器解析
        XMLScriptBuilder builder = new XMLScriptBuilder(configuration, script, parameterType);
        return builder.parseScriptNode();
    }

    /**
     * step-12 新增方法，用于处理注解配置 SQL 语句
     */
    @Override
    public SqlSource createSqlSource(Configuration configuration, String script, Class<?> parameterType) {
        // 暂时不解析动态 SQL
        return new RawSqlSource(configuration, script, parameterType);
    }

    @Override
    public ParameterHandler createParameterHandler(MappedStatement mappedStatement, Object parameterObject, BoundSql boundSql) {
        return new DefaultParameterHandler(mappedStatement, parameterObject, boundSql);
    }

}