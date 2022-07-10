package com.myspring.spring.entity;

import com.myspring.customSpring.Component;
import com.myspring.customSpring.Value;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author clearlove
 * @ClassName Order.java
 * @Description
 * @createTime 2021年09月06日 21:56:00
 */
@Data
@AllArgsConstructor
@Component("myOrder")
public class Order {

    @Value("123")
    private String orderId;

    @Value("1000.0")
    private Float price;
}
