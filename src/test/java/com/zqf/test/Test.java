package com.zqf.test;

import java.io.File;
import java.io.FileFilter;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class Test {

    private ClassLoader classLoader = Test.class.getClassLoader();//默认使用的类加载器
    @org.junit.Test
    public void test1(){
//        String  path = System.getProperty("user.dir");
//
//        System.out.println("user.dir：" + path);
//        String  urlStr = path.replace(".", "/");
//        URL url = classLoader.getResource(urlStr);
//        System.out.println("getProtocol"+url.getProtocol());
//
//        findClassLocal(path);
    }

    private void findClassLocal(final String packName){
        URI url = null ;
        try {
            url = classLoader.getResource(packName.replace(".", "/")).toURI();
        } catch (URISyntaxException e1) {
            throw new RuntimeException("未找到策略资源");
        }

        File file = new File(url);
        file.listFiles(new FileFilter() {

            public boolean accept(File chiFile) {
                if(chiFile.isDirectory()){
                    findClassLocal(packName+"."+chiFile.getName());
                }
                if(chiFile.getName().endsWith(".class")){
                    Class<?> clazz = null;
                    try {
                        clazz = classLoader.loadClass(packName + "." + chiFile.getName().replace(".class", ""));
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    System.out.println(chiFile);
//                    if(superStrategy.isAssignableFrom(clazz)){
//                        eleStrategyList.add((Class<? extends String>) clazz);
//                    }
                    return true;
                }
                return false;
            }
        });

    }

}
