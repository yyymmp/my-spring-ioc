package com.myspring.design.combat.strategy.practice;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

/**
 * @author jlz
 * @date 2023年05月22日 22:57
 */
@Component("jia")
public class AHanle extends Handle implements InitializingBean {

    @Override
    public Handle A() {
        System.out.println("处理a 逻辑");
        return this;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Factory.register("jia", this);
    }
}
