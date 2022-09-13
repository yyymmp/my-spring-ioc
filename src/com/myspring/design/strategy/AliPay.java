package com.myspring.design.strategy;

/**
 * @author jlz
 * @date 2022年07月17日 16:58
 */
public class AliPay implements PayStrategy {

    private String strategyId = "ali";

    @Override
    public void pay() {
        System.out.println("对接阿里支付");
    }

    @Override
    public String getStrategyId() {
        return strategyId;
    }
}
