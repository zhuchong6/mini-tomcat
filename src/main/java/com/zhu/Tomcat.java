package com.zhu;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author by zhuhcong
 * @descr
 * @date 2023/1/6 23:43
 */
public class Tomcat {


    public void start(){
        //Socket 链接
        try {
            //访问http://localhost:8080
            ServerSocket serverSocket = new ServerSocket(8080);

            ExecutorService executorService = Executors.newFixedThreadPool(10);

            //支持多个socket连接请求，如果不加循环的话，第一次执行完，就会结束vm
            while(true){
                Socket socket = serverSocket.accept();
                //用其他线程去做一些解析或者其他工作，主线程只用于接收请求
                executorService.execute(new SocketProcessor(socket));
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }



    public static void main(String[] args) {
        Tomcat tomcat = new Tomcat();

        tomcat.start();
    }
}