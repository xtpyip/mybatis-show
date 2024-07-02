package com.pyip.mybatis.design.singleton;

import java.io.Serializable;

// 饿汉式-静态变量
public class Singleton1  {
    private static Singleton1 singleton;
    private Singleton1(){

    }
    private static Singleton1  getSingleton(){
        return singleton;
    }
}

// 饿汉式-静态代码块
 class Singleton2 {
    private static Singleton2 instance;
    static {
        instance = new Singleton2();
    }
    private Singleton2(){}
    private static Singleton2 getInstance(){
        return instance;
    }
}
// 饿汉式-枚举类
// 枚举类型是线程安全的，并且只会装载一次，设计者充分的利用了枚举的这个特性来实现单例模式，
// 枚举的写法非常简单，而且枚举类型是所用单例实现中唯一一种不会被破坏的单例实现模式。
enum Singleton7 {
    INSTANCE;
}

