package com.myspring.design.combat.strategy.practice;


import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author jlz
 * @date 2023年05月22日 23:01
 */
public class Client {

    public static void main(String[] args) {
        String name = "jia";
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        //执行业务逻辑A时
        //Handle handle = (Handle)context.getBean(name);
        Handle handle = Factory.getStrategy(name);
        handle.A().B(name).other();

        //执行业务逻辑B时
        //handle.B(name);

    }
}
