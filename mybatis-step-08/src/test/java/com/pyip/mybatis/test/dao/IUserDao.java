package com.pyip.mybatis.test.dao;

import com.pyip.mybatis.test.po.User;

public interface IUserDao {

    User queryUserInfoById(Long uId);

}
