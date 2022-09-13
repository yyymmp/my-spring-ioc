package com.myspring.design.strategy;

/**
 * @author jlz
 * @date 2022年07月17日 16:59
 */
public class BankPay implements PayStrategy{

    private String strategyId = "bank" ;

    @Override
    public void pay() {
        System.out.println("银行支付");
    }

    @Override
    public String getStrategyId() {
        return strategyId;
    }
}
