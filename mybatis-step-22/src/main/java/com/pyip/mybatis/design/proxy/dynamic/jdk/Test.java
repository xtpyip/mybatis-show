package com.pyip.mybatis.design.proxy.dynamic.jdk;

import com.pyip.mybatis.design.proxy.statics.SellTickets;

/**
 * @ClassName: Test
 * @version: 1.0
 * @Author: pyipXt
 * @Description: 测试Jdk动态代理
 **/
public class Test {
    public static void main(String[] args) {
        ProxyFactory factory = new ProxyFactory();
        SellTickets proxyObject = factory.getProxyObject();
        proxyObject.sell();
    }
}
