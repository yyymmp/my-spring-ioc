package com.myspring.source;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author jlz
 * @date 2023年05月07日 21:27
 */
@Service
public class UserService {
    private OrderService orderService;

    @Autowired
    public UserService(OrderService orderService){
        this.orderService = orderService;
    }

    public void test(){
        System.out.println("test");
    }
}
