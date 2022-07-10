package com.myspring.design.factory;


/**
 * @author jlz
 * @date 2022年07月03日 23:41
 */
public class CoffeeStore {

    private AbstractFactory factory;


    public void setFactory(AbstractFactory factory) {
        this.factory = factory;
    }

    public Coffee get(){
        return factory.create();
    }
}
