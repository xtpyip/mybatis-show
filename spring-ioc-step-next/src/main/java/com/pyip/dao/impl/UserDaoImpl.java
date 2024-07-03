package com.pyip.dao.impl;

import com.pyip.dao.UserDao;

public class UserDaoImpl implements UserDao {

    private String username;
    private String password;

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserDaoImpl() {
        System.out.println("userDao被创建了");
    }

    public void add() {
        System.out.println("UserDao ..." + username + "==" + password);
    }
}