package com.myspring.design.factory.ab;

/**
 * @author jlz
 * @date 2022年07月12日 20:51
 */
public class SendClient {

    AbstractFactory abstractFactory;

    public void setAbstractFactory(AbstractFactory abstractFactory) {
        this.abstractFactory = abstractFactory;
    }


    public SendClient(AbstractFactory abstractFactory) {
        this.abstractFactory = abstractFactory;
    }

    public AbstractFactory getAbstractFactory() {
        return abstractFactory;
    }
}
