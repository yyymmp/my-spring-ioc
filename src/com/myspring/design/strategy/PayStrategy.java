package com.myspring.design.strategy;

/**
 * @author jlz
 * @date 2022年07月17日 16:57
 */
public interface PayStrategy {

    String getStrategyId();

    void pay();
}
