package com.myspring.design.factory.ab;

/**
 * @author jlz
 * @date 2022年07月12日 20:46
 */
public class SmsMsg extends MsgTemplate{

    @Override
    void send() {
        System.out.println("发送短信消息");
    }

    @Override
    void restore() {
        System.out.println("撤回短信消息");
    }
}
