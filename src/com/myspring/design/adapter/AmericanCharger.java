package com.myspring.design.adapter;

/**
 * 现有功能
 * @author jlz
 * @date 2022年07月14日 22:33
 */
public class AmericanCharger implements Adaptee{

    @Override
    public void chargeBy110V() {
        System.out.println("110v 充电");
    }
}
