package com.pyip.mybatis.design.singleton.problem;

import java.io.*;

class Singleton implements Serializable {
    //私有构造方法
    private Singleton() {}
    private static class SingletonHolder {
        private static final Singleton INSTANCE = new Singleton();
    }
    //对外提供静态方法获取该对象
    public static Singleton getInstance() {
        return SingletonHolder.INSTANCE;
    }
}
public class SerializableProblem  {
    public static void main(String[] args) throws Exception{
        //往文件中写对象
//        writeObject2File();
        //从文件中读取对象
        Singleton s1 = readObjectFromFile();
        Singleton s2 = readObjectFromFile();

//        判断两个反序列化后的对象是否是同一个对象
        System.out.println(s1 == s2);
    }
    private static Singleton readObjectFromFile() throws Exception {
        //创建对象输入流对象
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream("C:\\Users\\pyip2\\Desktop\\a.txt"));
        //第一个读取Singleton对象
        Singleton instance = (Singleton) ois.readObject();

        return instance;
    }
    public static void writeObject2File() throws Exception {
        //获取Singleton类的对象
        Singleton instance = Singleton.getInstance();
        //创建对象输出流
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("C:\\Users\\pyip2\\Desktop\\a.txt"));
        //将instance对象写出到文件中
        oos.writeObject(instance);
    }
}

class SingletonSolve implements Serializable {
    //私有构造方法
    private SingletonSolve() {}
    private static class SingletonHolder {
        private static final SingletonSolve INSTANCE = new SingletonSolve();
    }
    //对外提供静态方法获取该对象
    public static SingletonSolve getInstance() {
        return SingletonHolder.INSTANCE;
    }
    // 下面是为了解决序列化反序列化破解单例模式
    private Object readResolve() {
        return SingletonHolder.INSTANCE;
    }
}