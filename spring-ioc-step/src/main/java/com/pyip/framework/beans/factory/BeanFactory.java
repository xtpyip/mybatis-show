package com.pyip.framework.beans.factory;

public interface BeanFactory {

    Object getBean(String name) throws Exception;

    <T> T getBean(String name, Class<? extends T> clazz) throws Exception;
}