package com.zhu;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * @author by zhuhcong
 * @descr
 * @date 2023/1/9 00:06
 */
public class Response extends AbstractResponse{

    private int status = 200;
    private String message = "OK";

    private byte SP = ' ';
    private byte CR = '\r';
    private byte LF = '\n';
    private Map<String, String> headers = new HashMap<>();

    private Request request;
    private OutputStream socketOutputSteam;
    ResponseServletOutputStream responseServletOutputStream = new ResponseServletOutputStream();

    public Response(Request request) {
        this.request = request;
        try {
            this.socketOutputSteam = request.getSocket().getOutputStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int getStatus() {
        return status;
    }

    @Override
    public void setStatus(int status, String message) {
        this.status = status;
        this.message = message;
    }

    @Override
    public void addHeader(String s, String s1) {
        headers.put(s, s1);
    }

    @Override
    public ResponseServletOutputStream getOutputStream() throws IOException {
        return responseServletOutputStream;
    }

    /**
     * 实际发送响应的地方
     * 也是实现http response协议格式的地方
     */
    public void complete(){
        try {
            //发送响应行
            sendResponseLine();
            //发送响应头
            sendResponseHeader();
            //发送响应体
            sendResponseBody();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

    private void sendResponseBody() throws IOException {
        socketOutputSteam.write(getOutputStream().getBytes());
    }

    private void sendResponseHeader() throws IOException{
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            socketOutputSteam.write(key.getBytes());
            socketOutputSteam.write(":".getBytes());
            socketOutputSteam.write(value.getBytes());
            socketOutputSteam.write(CR);
            socketOutputSteam.write(LF);
        }
        socketOutputSteam.write(CR);
        socketOutputSteam.write(LF);
    }

    private void sendResponseLine() throws IOException {
        socketOutputSteam.write(request.getProtocol().getBytes());
        socketOutputSteam.write(SP);
        socketOutputSteam.write(status);
        socketOutputSteam.write(SP);
        socketOutputSteam.write(message.getBytes());
        socketOutputSteam.write(CR);
        socketOutputSteam.write(LF);
    }
}