package com.myspring.design.strategy;

import java.util.HashMap;
import java.util.Map;

/**
 * @author jlz
 * @date 2022年07月17日 17:00
 */
public class StrategyContext {

    static Map<String, PayStrategy> map = new HashMap<>();

    static {
        AliPay aliPay = new AliPay();
        map.put(aliPay.getStrategyId(),aliPay);
        BankPay bankPay = new BankPay();
        map.put(bankPay.getStrategyId(),bankPay);
    }

    public  static PayStrategy getPayStrategy(String strategyId){
        return map.get(strategyId);
    }
}
