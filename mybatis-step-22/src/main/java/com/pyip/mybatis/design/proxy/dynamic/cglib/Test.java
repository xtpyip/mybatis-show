package com.pyip.mybatis.design.proxy.dynamic.cglib;

/**
 * @ClassName: Test
 * @version: 1.0
 * @Author: pyipXt
 * @Description: 测试类
 **/
public class Test {
    public static void main(String[] args) {
        ProxyFactory factory = new ProxyFactory();
        TrainStation proxyObject = factory.getProxyObject();
        proxyObject.sell();
    }
}
