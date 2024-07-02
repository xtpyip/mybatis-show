package com.pyip.mybatis.design.proxy.dynamic.jdk;

import com.pyip.mybatis.design.proxy.statics.SellTickets;
import com.pyip.mybatis.design.proxy.statics.TrainStation;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @ClassName: ProxyFactory
 * @version: 1.0
 * @Author: pyipXt
 * @Description: 代理工厂，生产代理对象
 **/
public class ProxyFactory {
    // ProxyFactory不是代理模式中所说的代理类，而代理类是程序在运行过程中动态的在内存中生成的类。
    // 通过阿里巴巴开源的 Java 诊断工具（Arthas【阿尔萨斯】）查看代理类的结构：
    // * 代理类（$Proxy0）实现了SellTickets。这也就印证了我们之前说的真实类和代理类实现同样的接口。
    // * 代理类（$Proxy0）将我们提供了的匿名内部类对象传递给了父类。
    private TrainStation trainStation = new TrainStation();
    public SellTickets getProxyObject(){
        SellTickets sellTickets = (SellTickets) Proxy.newProxyInstance(
                trainStation.getClass().getClassLoader(),
                trainStation.getClass().getInterfaces(),
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        System.out.println("代售点收取费用（JDK动态代理）");
                        // 执行方法
                        Object result = method.invoke(trainStation, args);
                        return result;
                    }
                }
        );
        return sellTickets;
    }
}
