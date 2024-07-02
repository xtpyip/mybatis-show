package com.pyip.mybatis.design.proxy.statics;

/**
 * @ClassName: ProxyPoint
 * @version: 1.0
 * @Author: pyipXt
 * @Description: 代售商实现卖票接口
 **/
public class ProxyPoint implements SellTickets{
    TrainStation trainStation = new TrainStation();
    @Override
    public void sell() {
        System.out.println("代售点收取费用");
        trainStation.sell();
    }
}
