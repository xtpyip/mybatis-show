package com.pyip.mybatis.design.adapter.object;

public interface SDCard {
    //读取SD卡方法
    String readSD();
    //写入SD卡功能
    void writeSD(String msg);
}