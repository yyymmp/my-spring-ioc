package com.myspring.dao;

import java.util.Arrays;
import java.util.List;

/**
 * @author clearlove
 * @ClassName HelloDao.java
 * @Description
 * @createTime 2021年09月05日 14:44:00
 */
public class HelloDaoImpl implements HelloDao {


    public List<String> findAll() {

        return Arrays.asList("1", "2", "3");
    }
}
