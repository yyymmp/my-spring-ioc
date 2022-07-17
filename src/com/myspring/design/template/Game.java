package com.myspring.design.template;

/**
 * @author jlz
 * @date 2022年07月17日 16:05
 */
public abstract class Game {

    /**
     * 定义骨架
     */
    final void paly(){
        prepare();
        start();
        finish();

    }

    /**
     * 定义子类实现
     */
    abstract void prepare();

    abstract void start();

    abstract void finish();
}
