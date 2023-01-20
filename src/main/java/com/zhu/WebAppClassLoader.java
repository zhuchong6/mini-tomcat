package com.zhu;

import java.net.URL;
import java.net.URLClassLoader;

/**
 * @author by zhuhcong
 * @descr
 * @date 2023/1/21 01:13
 */
public class WebAppClassLoader extends URLClassLoader {
    public WebAppClassLoader(URL[] urls) {
        super(urls);
    }
}