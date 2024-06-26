package com.pyip.mybatis;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

public class Resources {
    // resource : mapper/User_Mapper.xml
    public static Reader getResourceAsReader(String resource) throws IOException {
        return new InputStreamReader(getResourceAsStream(resource));
    }
    private static InputStream getResourceAsStream(String resource) throws IOException {
        ClassLoader[] classLoaders = getClassLoaders();
        for (ClassLoader classLoader : classLoaders) {
            // 找到resource资源所在位置，并将其读取为输入流
            InputStream inputStream = classLoader.getResourceAsStream(resource);
            // 不为空，说明找到mapper/User_Mapper.xml此代表的输入流，返回即可
            if (null != inputStream) {
                return inputStream;
            }
        }
        throw new IOException("Could not find resource " + resource);
    }

    private static ClassLoader[] getClassLoaders() {
        return new ClassLoader[]{
                //获取系统类加载器
                ClassLoader.getSystemClassLoader(),
                //用来获取线程的上下文类加载器
                Thread.currentThread().getContextClassLoader()};
    }
}
