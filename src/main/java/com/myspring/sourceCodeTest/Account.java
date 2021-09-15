package com.myspring.sourceCodeTest;


import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author clearlove
 * @ClassName Account.java
 * @Description
 * @createTime 2021年09月06日 21:55:00
 */
@Data
@Component
public class Account {

////    @Value("1")
//    private Integer id;
////    @Value("张三")
//    private String name;
////    @Value("22")
//    private Integer age;
//
////    @Autowired
////    private Order Order;

    public void test() {
        System.out.println("test");
    }

}
