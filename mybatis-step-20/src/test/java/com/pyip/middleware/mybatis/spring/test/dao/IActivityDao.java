package com.pyip.middleware.mybatis.spring.test.dao;

import com.pyip.middleware.mybatis.spring.test.po.Activity;

public interface IActivityDao {

    Activity queryActivityById(Activity activity);

}
