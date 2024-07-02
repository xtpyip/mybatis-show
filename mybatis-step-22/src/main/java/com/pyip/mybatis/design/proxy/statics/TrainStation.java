package com.pyip.mybatis.design.proxy.statics;

/**
 * @ClassName: TrainStation
 * @version: 1.0
 * @Author: pyipXt
 * @Description: 火车站类实现卖票接口
 **/
public class TrainStation implements SellTickets{
    @Override
    public void sell() {
        System.out.println("火车站卖票");
    }
}
