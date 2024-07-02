package com.pyip.mybatis.design.singleton.problem;

import java.lang.reflect.Constructor;

class SingletonTemp {
    //私有构造方法
    private SingletonTemp() {}
    private static volatile SingletonTemp instance;
    //对外提供静态方法获取该对象
    public static SingletonTemp getInstance() {
        if(instance != null) {
            return instance;
        }
        synchronized (SingletonTemp.class) {
            if(instance != null) {
                return instance;
            }
            instance = new SingletonTemp();
            return instance;
        }
    }
}
public class ReflectProblem {
    public static void main(String[] args) throws Exception {
        //获取Singleton类的字节码对象
        Class clazz = SingletonTemp.class;
        //获取Singleton类的私有无参构造方法对象
        Constructor constructor = clazz.getDeclaredConstructor();
        //取消访问检查
        constructor.setAccessible(true);

        //创建Singleton类的对象s1
        SingletonTemp s1 = (SingletonTemp) constructor.newInstance();
        //创建Singleton类的对象s2
        SingletonTemp s2 = (SingletonTemp) constructor.newInstance();

        //判断通过反射创建的两个Singleton对象是否是同一个对象
        System.out.println(s1 == s2);
    }
}

class SingletonTempSolve {
    //私有构造方法
    private SingletonTempSolve() {
        // 反射破解单例模式需要添加的代码
        if(instance != null) {
            throw new RuntimeException();
        }
    }
    private static volatile SingletonTempSolve instance;
    //对外提供静态方法获取该对象
    public static SingletonTempSolve getInstance() {
        if(instance != null) {
            return instance;
        }
        synchronized (SingletonTempSolve.class) {
            if(instance != null) {
                return instance;
            }
            instance = new SingletonTempSolve();
            return instance;
        }
    }
}
