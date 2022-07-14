package com.myspring.design.adapter;

/**
 * @author jlz
 * @date 2022年07月14日 22:34
 */
public class Adapter implements Target{

    private Adaptee adaptee;

    public void setAdaptee(Adaptee adaptee) {
        this.adaptee = adaptee;
    }

    @Override
    public void chargeBy220V() {
        adaptee.chargeBy110V();
        System.out.println("再加110v,可以充电");
    }
}
