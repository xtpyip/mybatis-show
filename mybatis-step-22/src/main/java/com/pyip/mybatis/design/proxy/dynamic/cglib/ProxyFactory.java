package com.pyip.mybatis.design.proxy.dynamic.cglib;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * @ClassName: ProxyFactory
 * @version: 1.0
 * @Author: pyipXt
 * @Description: CGLIB实现动态代理
 **/
public class ProxyFactory implements MethodInterceptor {
    private TrainStation trainStation = new TrainStation();
    public TrainStation getProxyObject(){
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(trainStation.getClass());
        enhancer.setCallback(this);
        TrainStation object = (TrainStation) enhancer.create();
        return object;
    }
    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        System.out.println("代售点收取费用（CGLIB）");
        TrainStation result = (TrainStation) methodProxy.invokeSuper(o,objects);
        return result;
    }
}
