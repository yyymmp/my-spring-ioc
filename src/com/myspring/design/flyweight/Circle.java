package com.myspring.design.flyweight;

import lombok.Data;

/**
 * @author jlz
 * @date 2022年07月17日 13:10
 */
@Data
public class Circle implements Shape{

    /**
     * 外部状态 传入
     */
    private String color;


    public Circle(String color) {
        this.color = color;
    }

    @Override
    public void draw() {
        System.out.println("画出图形.颜色是"+color);
    }
}
