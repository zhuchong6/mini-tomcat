package com.zhu;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import java.io.IOException;
import java.net.Socket;

/**
 * @author by zhuhcong
 * @descr
 * @date 2023/1/9 00:14
 */
public class ResponseServletOutputStream extends ServletOutputStream {

    //暂时存储到这个字节数组中，因为response会可能会调多次write，所以有个缓冲，最后一起发送
    private byte[] bytes = new byte[2048];
    //截止的下标
    private int position = 0;

    @Override
    public void write(int b) throws IOException {
        bytes[position] = (byte) b;
        position++;
    }


    public byte[] getBytes() {
        return bytes;
    }

    public int getPosition() {
        return position;
    }

    @Override
    public boolean isReady() {
        return false;
    }

    @Override
    public void setWriteListener(WriteListener writeListener) {

    }
}