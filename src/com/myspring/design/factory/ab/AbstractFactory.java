package com.myspring.design.factory.ab;

import com.myspring.design.factory.Coffee;

/**
 * 抽象工厂
 * @author jlz
 * @date 2022年07月03日 23:37
 */
public abstract class AbstractFactory {

    /**
     * 让下面的工厂子类具体实现
     * @return
     */
    abstract MsgTemplate msgTemplate();
}
