package com.myspring.design.combat.strategy.practice;

/**
 * 模板方法设计模式
 * 顶层接口不需要具体实现 提供默认即可
 * @author jlz
 * @date 2023年05月22日 22:54
 */
public abstract class Handle{
    public Handle A(){
        //throw new UnsupportedOperationException();
        return this;
    }

    public Handle B(String name){
        //throw new UnsupportedOperationException();
        return this;
    }

    public Handle other(){
        return this;
    }

}
