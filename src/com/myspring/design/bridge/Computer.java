package com.myspring.design.bridge;

/**
 * 抽象电脑
 * @author jlz
 * @date 2022年07月15日 13:07
 */
public abstract class Computer {

    /**
     * 组合方式聚合品牌
     */
    protected Brand brand;

    public Computer(Brand brand) {
        this.brand = brand;
    }

    public void setBrand(Brand brand) {
        this.brand = brand;
    }

    public void info(){
        //
        brand.info();
    }
}
