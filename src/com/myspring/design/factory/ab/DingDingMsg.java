package com.myspring.design.factory.ab;

/**
 * @author jlz
 * @date 2022年07月12日 20:40
 */
public  class DingDingMsg extends MsgTemplate{

    public DingDingMsg() {
    }

    @Override
    void send() {
        System.out.println("发送钉钉消息");
    }

    @Override
    void restore() {
        System.out.println("发送钉钉消息");
    }
}
