package com.wangqi.service.impl;

import com.wangqi.dao.UserDao;
import com.wangqi.entity.PageCondition;
import com.wangqi.entity.PageResult;
import com.wangqi.pojo.User;
import com.wangqi.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserDao dao;
    @Override
    public User login(User userLogin) {
        return dao.login(userLogin);
    }

    @Override
    public String register(User user) {
       return dao.register(user);
    }


}
