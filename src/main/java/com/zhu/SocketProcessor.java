package com.zhu;

import com.zz.SelfServlet;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

/**
 * @author by zhuhcong
 * @descr
 * @date 2023/1/7 12:14
 */
public class SocketProcessor implements Runnable{

    private Socket socket;

    private Tomcat tomcat;

    public SocketProcessor(Socket socket) {
        this.socket = socket;
    }

    public SocketProcessor(Socket socket, Tomcat tomcat) {
        this.socket = socket;
        this.tomcat = tomcat;
    }

    private void processSocket(Socket socket) {
        //处理socket连接，解析读取的数据，写返回的数据
        try {
            InputStream inputStream = socket.getInputStream();
            //读取1kb的数据到bytes数组，暂时简单测试，正式中应该用循环读取
            byte[] bytes = new byte[2048];
            inputStream.read(bytes);


            //解析http请求，这里简单化处理只请求第一行内容，即请求方法，请求路径，协议版本
            //GET /hello HTTP/1.1
            Request request = extracted(bytes, new Request());
            request.setSocket(socket);
            Response response = new Response(request);

//            //这里自定义Servlet模拟用户写的servlet，暂时没写到tomcat加载webapps的servlet出此下策
//            SelfServlet selfServlet = new SelfServlet();
//            selfServlet.service(request, response);

            //通过url 找到对应的servlet
            String requestUrl = request.getRequestURL().toString();
            System.out.println("requestUrl = " + requestUrl);
            requestUrl = requestUrl.substring(1);
            String[] part = requestUrl.split("/");
            String appName = part[0];
            if(part.length>1){
                String urlPattern = part[1];

                Context context = tomcat.getContextMap().get(appName);
                if(context != null){
                    Servlet servlet = context.getByUrlPattern(urlPattern);
                    if(servlet != null){
                        servlet.service(request, response);
                        //发送响应
                        response.complete();
                    }else{
                        //servlet为空，搞一个默认的servlet
                        DefaultServlet defaultServlet = new DefaultServlet();
                        defaultServlet.service(request,response);
                        //发送响应
                        response.complete();
                    }
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ServletException e) {
            throw new RuntimeException(e);
        }

    }

    private static Request extracted(byte[] bytes, Request request) {
        //解析GET
        int endPosition  = 0;
        StringBuilder stringBuilder = new StringBuilder();
        for(; endPosition< bytes.length; endPosition++){
            char currentChar = (char) bytes[endPosition];
            if( currentChar == ' '){
                break;
            }
            stringBuilder.append(currentChar);
        }


        String method = stringBuilder.toString();
        request.setMethod(method);

        //清空暂存
        stringBuilder.delete(0, stringBuilder.length());
        //解析URL
        endPosition++;
        for(; endPosition< bytes.length; endPosition++){
            char currentChar = (char) bytes[endPosition];
            if( currentChar == ' '){
                break;
            }
            stringBuilder.append(currentChar);
        }

        String url = stringBuilder.toString();
        request.setUrl(url);


        //清空暂存
        stringBuilder.delete(0, stringBuilder.length());
        //解析URL
        endPosition++;
        for(; endPosition< bytes.length; endPosition++){
            char currentChar = (char) bytes[endPosition];
            if( currentChar == '\r'){
                break;
            }
            stringBuilder.append(currentChar);
        }
        String protocal = stringBuilder.toString();
        request.setProtocol(protocal);
        return request;
    }

    @Override
    public void run() {
        processSocket(socket);
    }
}