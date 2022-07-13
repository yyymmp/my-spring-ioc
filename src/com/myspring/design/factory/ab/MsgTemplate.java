package com.myspring.design.factory.ab;

/**
 * @author jlz
 * @date 2022年07月12日 20:40
 */
public abstract class MsgTemplate {

    /**
     * 消息发送
     */
    abstract void send();

    /**
     * 消息撤回
     */
    abstract void restore();

}
