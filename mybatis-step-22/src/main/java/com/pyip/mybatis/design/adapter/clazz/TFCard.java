package com.pyip.mybatis.design.adapter.clazz;

public interface TFCard {
    //读取TF卡方法
    String readTF();
    //写入TF卡功能
    void writeTF(String msg);
}