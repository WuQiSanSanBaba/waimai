package com.wangqi.dao;

import com.wangqi.entity.PageCondition;
import com.wangqi.entity.PageResult;
import com.wangqi.pojo.User;
//实现类有更详细的注解
public interface UserDao {
    //登录
    User login(User userLogin);
    //注册
    String register(User user);

}
