package com.zz;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author by zhuhcong
 * @descr 测试的servlet
 * @date 2023/1/8 23:55
 */
public class SelfServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("user servlet:"+req.getMethod());
        resp.addHeader("Content-Length", "12");
        resp.addHeader("Content-Type", "text/plain;charset=utf-8");
        resp.getOutputStream().write("hello , mini-tomcat".getBytes());
    }

}