package com.pyip.mybatis.design.singleton.jdk;

import java.io.IOException;
import java.io.InputStream;

/**
  public class Runtime {
  private static Runtime currentRuntime = new Runtime();
  public static Runtime getRuntime(){
        return currentRuntime;
  }
  private Runtime(){}
 */
public class RuntimeDemo {
    public static void main(String[] args) throws IOException {
        //获取Runtime类对象
        Runtime runtime = Runtime.getRuntime();

        //返回 Java 虚拟机中的内存总量。
        System.out.println(runtime.totalMemory());
        //返回 Java 虚拟机试图使用的最大内存量。
        System.out.println(runtime.maxMemory());

        //创建一个新的进程执行指定的字符串命令，返回进程对象
        Process process = runtime.exec("ipconfig");
        //获取命令执行后的结果，通过输入流获取
        InputStream inputStream = process.getInputStream();
        byte[] arr = new byte[1024 * 1024 * 100];
        int b = inputStream.read(arr);
        System.out.println(new String(arr, 0, b, "gbk"));
    }
}
