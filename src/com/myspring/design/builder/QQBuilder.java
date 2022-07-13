package com.myspring.design.builder;

/**
 * @author jlz
 * @date 2022年07月13日 20:09
 */
public class QQBuilder extends AbstractBuilder{
    protected ProductCar productCar = new ProductCar();

    @Override
    void setSetaNum() {
        System.out.println("qq汽车座位:4");
        productCar.setSetaNum("4");
    }

    @Override
    void setEngine() {
        System.out.println("qq汽车引擎:2.0T");
        productCar.setEngine("2.0T");
    }

    @Override
    void setGPS() {
        System.out.println("qq汽车gps:北斗");
        productCar.setEngine("北斗");
    }

    @Override
    public ProductCar builder() {
        return productCar;
    }
}
