package com.myspring.sourceCodeTest;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author clearlove
 * @ClassName Test.java
 * @Description
 * @createTime 2021年09月06日 22:03:00
 */
public class Test {

    public static void main(String[] args) {
        //传入被扫描的包
        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(SpringConfig.class);

        Account account = applicationContext.getBean("account", Account.class);
        account.test();

//        System.out.println(applicationContext.getBean("account"));
    }

}
