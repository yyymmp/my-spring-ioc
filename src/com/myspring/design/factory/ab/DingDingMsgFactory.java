package com.myspring.design.factory.ab;

/**
 * 具体实现的工厂
 * @author jlz
 * @date 2022年07月12日 20:46
 */
public class DingDingMsgFactory extends  AbstractFactory{

    @Override
    MsgTemplate msgTemplate() {
        return new DingDingMsg();
    }
}
