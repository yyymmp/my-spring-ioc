package com.myspring.sourceCodeTest;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * @author clearlove
 * @ClassName springConfig.java
 * @Description
 * @createTime 2021年09月14日 00:02:00
 */
@EnableAspectJAutoProxy
@ComponentScan("com.myspring.sourceCodeTest")
@Configuration
public class SpringConfig {

}
