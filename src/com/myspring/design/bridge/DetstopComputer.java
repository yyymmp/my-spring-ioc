package com.myspring.design.bridge;

/**
 * @author jlz
 * @date 2022年07月15日 13:10
 */
public class DetstopComputer extends Computer{

    public DetstopComputer(Brand brand) {
        super(brand);
    }

    @Override
    public void info() {
        System.out.println("台式机电脑");
        super.info();
    }
}
