package com.pyip.mybatis.test.dao;

import com.pyip.mybatis.test.po.Activity;

public interface IActivityDao {

    Activity queryActivityById(Long activityId);

}
