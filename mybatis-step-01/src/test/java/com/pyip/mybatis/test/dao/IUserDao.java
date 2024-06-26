package com.pyip.mybatis.test.dao;

import com.pyip.mybatis.test.po.User;

import java.util.List;

public interface IUserDao {
    // 根据id查询用户
    User queryUserInfoById(Long id);

    // 根据userId查询集合
    List<User> queryUserList(User user);
}
