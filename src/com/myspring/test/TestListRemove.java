package com.myspring.test;

import com.myspring.spring.entity.Order;
import java.util.ArrayList;
import java.util.List;

/**
 * @author clearlove
 * @ClassName TestListRemove.java
 * @Description
 * @createTime 2021年09月09日 22:21:00
 */
public class TestListRemove {

    public static void main(String[] args) {
        List<Order> list = new ArrayList<>();
        Order order = new Order("1", 1.1f);
        Order order1 = new Order("1", 1.1f);
        Order order2 = new Order("2", 1.1f);
        Order order3 = new Order("3", 1.1f);
        list.add(order);
        list.add(order1);
        list.add(order2);
        list.add(order3);
        System.out.println(list);

        list.remove(order);
        System.out.println(list);

    }
}
