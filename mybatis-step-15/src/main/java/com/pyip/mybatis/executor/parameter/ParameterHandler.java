package com.pyip.mybatis.executor.parameter;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @author 小傅哥，微信：fustack
 * @description 参数处理器
 * @github https://github.com/fuzhengwei
 * @Copyright 公众号：bugstack虫洞栈 | 博客：https://bugstack.cn - 沉淀、分享、成长，让自己和他人都能有所收获！
 */
public interface ParameterHandler {

    /**
     * 获取参数
     */
    Object getParameterObject();

    /**
     * 设置参数
     */
    void setParameters(PreparedStatement ps) throws SQLException;

}
