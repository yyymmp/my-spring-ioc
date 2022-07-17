package com.myspring.design.template;

/**
 * @author jlz
 * @date 2022年07月17日 16:11
 */
public class BasketballGame extends Game{

    @Override
    void prepare() {
        System.out.println("准备热身");
    }

    @Override
    void start() {
        System.out.println("开始投篮");
    }

    @Override
    void finish() {
        System.out.println("比赛结束");
    }
}
