package com.myspring.source.aspect;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

/**
 * @author jlz
 * @date 2023年05月07日 22:00
 */
@Component
@Aspect
public class UserAspect {

    @Before("execution(public void com.myspring.source.UserService.test())")
    public void before(){
        System.out.println("before");
    }
}
