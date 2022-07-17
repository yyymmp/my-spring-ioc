package com.myspring.design.template;

/**
 * @author jlz
 * @date 2022年07月17日 16:12
 */
public class PingPongGame extends Game{

    @Override
    void prepare() {
        System.out.println("猜拳获得场地");
    }

    @Override
    void start() {
        System.out.println("开始发球");
    }

    @Override
    void finish() {
        System.out.println("比赛结束");
    }
}
