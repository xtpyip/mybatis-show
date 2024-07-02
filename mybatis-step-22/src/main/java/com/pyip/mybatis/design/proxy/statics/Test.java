package com.pyip.mybatis.design.proxy.statics;

/**
 * @ClassName: Test
 * @version: 1.0
 * @Author: pyipXt
 * @Description: 测试类
 **/
public class Test {
    // ProxyPoint作为TrainStation的代理中介
    public static void main(String[] args) {
        ProxyPoint proxyPoint = new ProxyPoint();
        // 代售点卖票
        proxyPoint.sell();
    }
}
