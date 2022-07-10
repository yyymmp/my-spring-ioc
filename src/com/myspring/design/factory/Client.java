package com.myspring.design.factory;

/**
 * @author jlz
 * @date 2022年07月03日 23:51
 */
public class Client {

    public static void main(String[] args) {
        CoffeeStore coffeeStore = new CoffeeStore();

        //创建工厂
        AbstractFactory abstractFactory = new com.myspring.design.factory.ACoffeeFactory();
        coffeeStore.setFactory(abstractFactory);

        coffeeStore.get();
    }
}
