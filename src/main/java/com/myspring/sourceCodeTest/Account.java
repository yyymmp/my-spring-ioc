package com.myspring.sourceCodeTest;


import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author clearlove
 * @ClassName Account.java
 * @Description
 * @createTime 2021年09月06日 21:55:00
 */
@Data
@Component
public class Account {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private Account account;
////    @Value("1")
//    private Integer id;
////    @Value("张三")
//    private String name;
////    @Value("22")
//    private Integer age;
//
////    @Autowired
////    private Order Order;

    @Transactional
    public void test() {
        jdbcTemplate.execute("insert into age value(1,2)");
        System.out.println("test");
        account.a();
        throw new NullPointerException();

    }

    // Propagation.NEVER 调用a方法前已存在一个事务 则重新创建一个事务
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void a() {
        jdbcTemplate.execute("insert into age value(2,2)");
    }
}
