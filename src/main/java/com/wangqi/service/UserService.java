package com.wangqi.service;

import com.wangqi.entity.PageCondition;
import com.wangqi.entity.PageResult;
import com.wangqi.pojo.User;

public interface UserService {
    User login(User userlogin);
    String register(User user);


}
