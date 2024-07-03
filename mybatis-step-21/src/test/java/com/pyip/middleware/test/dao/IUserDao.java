package com.pyip.middleware.test.dao;

import com.pyip.middleware.test.po.User;

public interface IUserDao {

    User queryUserInfoById(Long uId);

}
