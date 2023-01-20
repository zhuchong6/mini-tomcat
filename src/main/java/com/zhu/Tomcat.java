package com.zhu;

import javax.servlet.Servlet;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author by zhuhcong
 * @descr
 * @date 2023/1/6 23:43
 */
public class Tomcat {

    /**
     * 保存多个app名称对应的Contex
     * 方便不同应用下访问对应的servlet，例如：localhost/zz/hello  localhost/zhu/hello
     */
    private Map<String, Context> contextMap = new HashMap<>();


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
                executorService.execute(new SocketProcessor(socket, this));
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }



    public static void main(String[] args) {
        Tomcat tomcat = new Tomcat();

        tomcat.deployApps();
        tomcat.start();
    }

    private void deployApps() {
        //user.dir获取当前的工作目录，在进入其子目录webapps
        File webapps = new File(System.getProperty("user.dir"), "webapps");
        for (String appName : webapps.list()) {

            deployApp(webapps, appName);
        }
    }

    /**
     * 部署app
     * @param webapps webapps目录
     * @param appName webapps下面的目录
     */
    private void deployApp(File webapps, String appName) {
        //创建一个项目的上下文，用于保存这个项目下url和servlet的映射关系
        Context context = new Context(appName);
        System.out.println(webapps.toString());

        //1.找到当前目录下有哪些servlet
        File appDirectory = new File(webapps, appName);
        File classesDirectory = new File(appDirectory, "classes");
        System.out.println(classesDirectory);

        List<File> allFiles = getAllFileFromAbsolutePath(classesDirectory);
        for (File file : allFiles) {
            //加载servlet类

            //将 /com/zz/t.class---> com.zz.t
            String name = file.getPath();
            //当前系统的文件分割符号
            String separator = File.separator;
            name = name.replace(classesDirectory.getPath()+separator, "");
            name = name.replace(".class", "");
            name = name.replace(separator, ".");

            System.out.println(name);

            //使用类加载器加载类
            Class<?> servletClazz = null;
            try {
                //注意不能使用当前线程的类加载器，因为我们要加载的文件不属于tomcat的工程，所以会提示找不到，这个时候只能使用自定义的类加载器
                //servletClazz = Thread.currentThread().getContextClassLoader().loadClass(name);
                WebAppClassLoader webAppClassLoader = new WebAppClassLoader(new URL[]{classesDirectory.toURL()});
                servletClazz = webAppClassLoader.loadClass(name);
                System.out.println(servletClazz);

                //判断类是否继承HttpServlet
                if(HttpServlet.class.isAssignableFrom(servletClazz)){
                    System.out.println(servletClazz);
                    //判断是否有WebServlet注解
                    if(servletClazz.isAnnotationPresent(WebServlet.class)){
                        WebServlet annotation = servletClazz.getAnnotation(WebServlet.class);
                        String[] strings = annotation.urlPatterns();

                        for (String urlPattern : strings) {
                            //将满足条件的servlet加入映射
                            context.addUrlPatternMapping(urlPattern, (Servlet) servletClazz.newInstance());
                        }
                    }

                }

            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }

        }

        contextMap.put(appName, context);
    }

    private static List<File> getAllFileFromAbsolutePath(File classesDirectory) {
        List<File> allFiles = new ArrayList<>();
        try {
            Files.walkFileTree(classesDirectory.toPath(), new SimpleFileVisitor<Path>(){
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    String filePath = file.toAbsolutePath().toString();
                    System.out.println(filePath);
                    File file1 = file.toAbsolutePath().toFile();
                    System.out.println(file1);
                    allFiles.add(file1);
                    return super.visitFile(file, attrs);
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return allFiles;
    }

    public Map<String, Context> getContextMap() {
        return contextMap;
    }

    public void setContextMap(Map<String, Context> contextMap) {
        this.contextMap = contextMap;
    }
}
