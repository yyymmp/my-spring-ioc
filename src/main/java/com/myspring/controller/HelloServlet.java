package com.myspring.controller;

import com.myspring.service.HelloService;
import com.myspring.service.HelloServiceImpl;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author clearlove
 * @ClassName HelloServlet.java
 * @Description
 * @createTime 2021年09月05日 14:19:00
 */
@WebServlet("/hello")
public class HelloServlet extends HttpServlet {

    public HelloService helloService = new HelloServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("hello");

        resp.getWriter().write(helloService.findAll().toString());
//        resp.getWriter().write("123456");

    }
}
