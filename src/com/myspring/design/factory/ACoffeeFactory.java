package com.myspring.design.factory;



/**
 * 具体工厂 专门生产ACoffee
 * @author jlz
 * @date 2022年07月03日 23:39
 */
public class ACoffeeFactory extends AbstractFactory {

    @Override
    Coffee create() {
        System.out.println("创建a coffee");
        return new ACoffee();
    }
}
