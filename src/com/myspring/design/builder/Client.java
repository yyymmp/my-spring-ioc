package com.myspring.design.builder;

/**
 * @author jlz
 * @date 2022年07月13日 20:44
 */
public class Client {

    public static void main(String[] args) {

        Director director = new Director();

        //需要什么 传入什么产品的builder  主要实现产品与创建过程分离
        BenzBuilder benzBuilder = new BenzBuilder();
        director.construct(benzBuilder);

        ProductCar car = benzBuilder.builder();

        System.out.println(car);

    }
}
