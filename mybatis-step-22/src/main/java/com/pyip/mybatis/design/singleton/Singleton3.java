package com.pyip.mybatis.design.singleton;
// 懒汉式-线程不安全
class Singleton3 {
    private Singleton3(){}
    private static Singleton3 instance;
    public static Singleton3 getInstance(){
        if(instance == null){
            instance = new Singleton3();
        }
        return instance;
    }
}
// 懒汉式，线程安全
class Singleton4 {
    private Singleton4(){}
    private static Singleton4 instance;
    public static synchronized Singleton4 getInstance(){
        if(instance == null){
            instance = new Singleton4();
        }
        return instance;
    }
}

// 懒汉式，双重检查锁
class Singleton5 {
    private Singleton5(){}
    // 在多线程的情况下，可能会出现空指针问题，出现问题的原因是JVM在实例化对象的时候会进行优化和指令重排序操作。
    private static volatile Singleton5 instance;
    public static Singleton5 getInstance(){
        if(instance == null){
            synchronized (Singleton5.class){
                if (instance == null)
                    instance = new Singleton5();
            }
        }
        return instance;
    }
}
// 懒汉式-静态内部类
class Singleton6 {
    //私有构造方法
    private Singleton6() {}

    private static class SingletonHolder {
        private static final Singleton6 INSTANCE = new Singleton6();
    }
    //对外提供静态方法获取该对象
    public static Singleton6 getInstance() {
        return SingletonHolder.INSTANCE;
    }
}

