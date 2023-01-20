package com.zhu;

import javax.servlet.Servlet;
import java.util.HashMap;
import java.util.Map;

/**
 * @author by zhuhcong
 * @descr 保存单个应用下的url和servlet映射关系
 * @date 2023/1/21 01:29
 */
public class Context {
    //app名称
    private String appName;

    //url 和 servlet的映射关系
    private Map<String , Servlet> urlPatternMapping = new HashMap<>();

    public Context(String appName) {
        this.appName = appName;
    }

    public void addUrlPatternMapping(String urlPattern, Servlet servlet){
        urlPatternMapping.put(urlPattern, servlet);
    }

    public Servlet getByUrlPattern(String urlPattern){

        for (String key : urlPatternMapping.keySet()) {
            if(key.contains(urlPattern)){
                return urlPatternMapping.get(key);
            }
        }
        return null;
    }
}