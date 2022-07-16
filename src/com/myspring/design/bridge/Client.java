package com.myspring.design.bridge;

/**
 * @author jlz
 * @date 2022年07月15日 13:10
 */
public class Client {

    public static void main(String[] args) {
        Brand brand = new Apple();
        Computer computer = new DetstopComputer(brand);
        computer.info();
        brand = new Huawei();

        computer.setBrand(brand);
        computer.info();
    }
}
