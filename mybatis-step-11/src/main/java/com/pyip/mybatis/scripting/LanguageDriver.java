package com.pyip.mybatis.scripting;

import com.pyip.mybatis.executor.parameter.ParameterHandler;
import com.pyip.mybatis.mapping.BoundSql;
import com.pyip.mybatis.mapping.MappedStatement;
import com.pyip.mybatis.mapping.SqlSource;
import com.pyip.mybatis.session.Configuration;
import org.dom4j.Element;

/**
 * @author 小傅哥，微信：fustack
 * @description 脚本语言驱动
 * @github https://github.com/fuzhengwei
 * @Copyright 公众号：bugstack虫洞栈 | 博客：https://bugstack.cn - 沉淀、分享、成长，让自己和他人都能有所收获！
 */
public interface LanguageDriver {

    /**
     * 创建SQL源码(mapper xml方式)
     */
    SqlSource createSqlSource(Configuration configuration, Element script, Class<?> parameterType);

    /**
     * 创建参数处理器
     */
    ParameterHandler createParameterHandler(MappedStatement mappedStatement, Object parameterObject, BoundSql boundSql);

}
