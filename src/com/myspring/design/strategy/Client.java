package com.myspring.design.strategy;

/**
 * @author jlz
 * @date 2022年07月17日 17:25
 */
public class Client {

    public static void main(String[] args) {
        //这里可以根据前端参数获取策略
        PayStrategy ali = StrategyContext.getPayStrategy("ali");
        ali.pay();
    }
}
