package com.myspring.design.builder;

/**
 * 抽象构建者模式: 定义对象统一建构方法
 * @author jlz
 * @date 2022年07月13日 19:59
 */

public abstract class AbstractBuilder {

    //创建产品具体流程
    abstract void setSetaNum();

    abstract void setEngine();

    abstract void setGPS();

    //获取产品
    abstract public ProductCar builder();
}
