package com.myspring.design.adapter;

/**
 * @author jlz
 * @date 2022年07月14日 22:46
 */
public class Client {

    public static void main(String[] args) {
        Adapter adapter = new Adapter();
        //已有美国充电110v
        Adaptee adaptee = new AmericanCharger();
        adapter.setAdaptee(adaptee);

        adapter.chargeBy220V();
    }
}
