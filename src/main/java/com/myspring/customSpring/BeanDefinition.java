package com.myspring.customSpring;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author clearlove
 * @ClassName BeanDefinition.java
 * @Description
 * @createTime 2021年09月07日 22:10:00
 */
@Data
@AllArgsConstructor
public class BeanDefinition {

    private String beanName;
    private Class beanClass;
}
