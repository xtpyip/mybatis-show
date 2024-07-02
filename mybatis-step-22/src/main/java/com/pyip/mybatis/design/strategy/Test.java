package com.pyip.mybatis.design.strategy;

/**
 * @ClassName: Test
 * @version: 1.0
 * @Author: pyipXt
 * @Description: 测试类
 **/
public class Test {
    public static void main(String[] args) {
        SalesMan salesMan = new SalesMan(new StrategyA());
        salesMan.salesManShow();
        System.out.println("=============");
        salesMan = new SalesMan(new StrategyB());
        salesMan.salesManShow();
        System.out.println("=============");
        salesMan = new SalesMan(new StrategyC());
        salesMan.salesManShow();

    }
}
