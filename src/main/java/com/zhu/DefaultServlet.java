package com.zhu;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author by zhuhcong
 * @descr
 * @date 2023/1/21 02:02
 */
public class DefaultServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("tomcat error servlet:"+req.getMethod());
        resp.addHeader("Content-Length", "12");
        resp.addHeader("Content-Type", "text/plain;charset=utf-8");
        resp.getOutputStream().write("hello , 404 mini-tomcat".getBytes());
    }
}