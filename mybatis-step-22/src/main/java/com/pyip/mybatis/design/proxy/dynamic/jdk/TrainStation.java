package com.pyip.mybatis.design.proxy.dynamic.jdk;

/**
 * @ClassName: TrainStation
 * @version: 1.0
 * @Author: pyipXt
 * @Description: 火车站卖票
 **/
public class TrainStation implements SellTickets{

    @Override
    public void sell() {
        System.out.println("火车站卖票");
    }
}
