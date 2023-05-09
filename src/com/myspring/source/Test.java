package com.myspring.source;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author jlz
 * @date 2023年05月07日 21:32
 */
public class Test {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);

        UserService userService = (UserService)context.getBean("userService");
        System.out.println(userService);
        userService.test();
    }
}
