package com.myspring.spring.entity;

import com.myspring.customSpring.Autowired;
import com.myspring.customSpring.Component;
import com.myspring.customSpring.Qualifier;
import com.myspring.customSpring.Value;
import lombok.Data;

/**
 * @author clearlove
 * @ClassName Account.java
 * @Description
 * @createTime 2021年09月06日 21:55:00
 */
@Data
//@Component
@Component
public class Account {

    @Value("1")
    private Integer id;
    @Value("张三")
    private String name;
    @Value("22")
    private Integer age;
    @Autowired
    @Qualifier("myOrder")
    private Order Order;

}
