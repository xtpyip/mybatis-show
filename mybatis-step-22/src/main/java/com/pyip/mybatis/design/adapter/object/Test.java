package com.pyip.mybatis.design.adapter.object;

/**
 * @ClassName: Test
 * @version: 1.0
 * @Author: pyipXt
 * @Description: 测试类
 **/
public class Test {
    public static void main(String[] args) {
        Computer computer = new Computer();
        SDCard sdCard = new SDCardImpl();
        System.out.println(computer.readSD(sdCard));

        System.out.println("------------");

        SDAdapterTF adapter = new SDAdapterTF(new TFCardImpl());
        System.out.println(computer.readSD(adapter));
    }
}
