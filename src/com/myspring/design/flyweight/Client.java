package com.myspring.design.flyweight;

/**
 * @author jlz
 * @date 2022年07月17日 13:15
 */
public class Client {

    public static void main(String[] args) {
        Shape color = ShapeFactory.get("color");

        color.draw();
    }
}
