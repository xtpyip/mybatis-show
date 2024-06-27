package com.pyip.mybatis.reflection.invoker;

/**
 * @author 小傅哥，微信：fustack
 * @description 调用者
 * @github https://github.com/fuzhengwei
 * @Copyright 公众号：bugstack虫洞栈 | 博客：https://bugstack.cn - 沉淀、分享、成长，让自己和他人都能有所收获！
 */
// 针对于Class中Field和Method的调用，在MyBatis中封装了Invoker对象来统一处理(有使用到适配器模式)
public interface Invoker {

    // 执行Field或者Method
    Object invoke(Object target, Object[] args) throws Exception;

    // 返回属性相应的类型
    Class<?> getType();

}
