package com.pyip.mybatis.design.factory.abstracts;


public class ItalyDessertFactory implements DessertFactory{
    public Coffee createCoffee() {
        return new LatteCoffee();
    }

    public Dessert createDessert() {
        return new Trimisu();
    }
}
