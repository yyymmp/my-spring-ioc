package com.myspring.design.bridge;

/**
 * 不同品牌的具体实现
 * @author jlz
 * @date 2022年07月15日 13:07
 */
public class Apple implements Brand{

    @Override
    public void info() {
        System.out.println("apple电脑");
    }
}
