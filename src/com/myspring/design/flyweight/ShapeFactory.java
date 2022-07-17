package com.myspring.design.flyweight;

import java.util.HashMap;
import java.util.Map;

/**
 * @author jlz
 * @date 2022年07月17日 13:12
 */
public class ShapeFactory {

    private static Map<String, Shape> map = new HashMap<>();


    public static Shape get(String color) {
        Shape circle = map.get(color);
        if (circle == null) {
            circle = new Circle(color);
            System.out.println("创建了一个图形");
            map.put(color, circle);
        }

        return (Circle) circle;
    }
}
