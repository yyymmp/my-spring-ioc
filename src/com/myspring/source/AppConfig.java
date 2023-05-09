package com.myspring.source;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * @author jlz
 * @date 2023年05月07日 21:31
 */
@Configuration
@ComponentScan("com.myspring.source")
@EnableAspectJAutoProxy
public class AppConfig {

}
