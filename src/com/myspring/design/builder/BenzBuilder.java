package com.myspring.design.builder;

/**
 * @author jlz
 * @date 2022年07月13日 20:27
 */
public class BenzBuilder extends AbstractBuilder{
    protected ProductCar productCar = new ProductCar();

    @Override
    void setSetaNum() {
        System.out.println("奔驰汽车座位:6");
        productCar.setSetaNum("6");
    }

    @Override
    void setEngine() {
        System.out.println("奔驰汽车引擎:4.0T");
        productCar.setEngine("4.0T");
    }

    @Override
    void setGPS() {
        System.out.println("奔驰汽车gps:北斗");
        productCar.setEngine("北斗");
    }

    @Override
    public ProductCar builder() {
        return productCar;
    }
}
