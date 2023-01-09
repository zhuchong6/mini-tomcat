package com.zhu;

import java.net.Socket;

/**
 * @author by zhuhcong
 * @descr 定义请求实体，暂时只保存以下三个参数
 * @date 2023/1/8 23:46
 */
public class Request extends AbstractRequest {

    private String method;
    private String url;
    private String protocol;

    private Socket socket;

    public Request() {
    }

    public Request(String method, String url, String protocol, Socket socket) {
        this.method = method;
        this.url = url;
        this.protocol = protocol;
        this.socket = socket;
    }

    public Socket getSocket() {
        return socket;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public StringBuffer getRequestURL() {
        return new StringBuffer(url);
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }
}