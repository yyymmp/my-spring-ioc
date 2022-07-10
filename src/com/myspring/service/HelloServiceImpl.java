package com.myspring.service;

import com.myspring.dao.HelloDao;
import com.myspring.factory.BeanFactory;
import java.util.List;

/**
 * @author clearlove
 * @ClassName HelloServiceImpl.java
 * @Description
 * @createTime 2021年09月05日 14:44:00
 */
public class HelloServiceImpl implements HelloService {
//直接使用new的方式
//    public HelloDao helloDaoImpl = new HelloDaoImpl();

    //使用静态工厂
//    public HelloDao helloDao = BeanFactory.getDao();

    //使用外部配置文件
    public HelloDao helloDao = (HelloDao) BeanFactory.getDao("helloDao");

    @Override
    public List<String> findAll() {
        return helloDao.findAll();
    }
}
