package com.myspring.sourceCodeTest;


import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author clearlove
 * @ClassName Order.java
 * @Description
 * @createTime 2021年09月06日 21:56:00
 */
@Data
@Component
public class Order {

    @Value("123")
    private String orderId;

    @Value("1000.0")
    private Float price;
}
