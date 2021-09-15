package com.myspring.sourceCodeTest;

import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * @author clearlove
 * @ClassName Aspect.java
 * @Description
 * @createTime 2021年09月13日 23:50:00
 */
@Aspect
@Component
public class MyAspect {

//    @Before("com.myspring.sourceCodeTest.Account.test()")
//    public void beforeTest() {
//        System.out.println("before");
//    }
    /**
     * 一个切入点，绑定到方法上
     */
//    @Pointcut("execution(* *(..))")
    @Pointcut("execution(* Account.test())")
    public void pointCut() {
    }


    /**
     * 一个前置通知，绑定到方法上
     */
    @Before("pointCut()")
    public void before() {
        System.out.println("----before advice----");
    }

    /**
     * 一个后置通知，绑定到方法上
     */
    @AfterReturning("pointCut()")
    public void afterReturning() {
        System.out.println("----afterReturning advice----");
    }
}
