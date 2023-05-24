package com.myspring.design.combat.strategy.practice;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

/**
 * @author jlz
 * @date 2023年05月22日 22:57
 */
@Component("li")
public class BHanle extends Handle implements InitializingBean {

    @Override
    public Handle B(String name) {
        System.out.println("处理b逻辑");
        return this;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Factory.register("li", this);
    }
}
