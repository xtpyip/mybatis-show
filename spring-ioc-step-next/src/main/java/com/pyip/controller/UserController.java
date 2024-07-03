package com.pyip.controller;

import com.pyip.framework.context.ApplicationContext;
import com.pyip.framework.context.support.ClassPathXmlApplicationContext;
import com.pyip.service.UserService;

public class UserController {
    public static void main(String[] args) throws Exception {
        //1,创建spring的容器对象
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("applicationContext.xml");
        //BeanFactory beanFactory = new XmlBeanFactory(new ClassPathResource("applicationContext.xml"));
        //2,从容器对象中获取userService对象
        UserService userService = applicationContext.getBean("userService", UserService.class);
        //3,调用userService方法进行业务逻辑处理
        userService.add();
    }
}