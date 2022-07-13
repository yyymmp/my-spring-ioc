package com.myspring.design.builder;

/**
 * @author jlz
 * @date 2022年07月13日 20:34
 */
public class Director {
    public void construct(AbstractBuilder builder){
        //导演调用创建过程
        builder.setEngine();
        builder.setSetaNum();
        builder.setGPS();
    }
}
