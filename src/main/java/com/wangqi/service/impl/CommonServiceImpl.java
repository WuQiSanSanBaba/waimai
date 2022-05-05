package com.wangqi.service.impl;

import com.wangqi.dao.CommonDao;
import com.wangqi.pojo.Store;
import com.wangqi.pojo.User;
import com.wangqi.service.CommonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Service
public class CommonServiceImpl implements CommonService {
    @Autowired
    CommonDao commonDao;
    @Override
    public List<String> getMessage() {
        return commonDao.getMessage();
    }

    @Override
    public List<Store> initCarousel(int count) {
        return commonDao.initCarousel(count);
    }

    @Override
    public List<Store> getAllStore() {
        return commonDao.getAllStore();
    }
    @Override
    public boolean reduceMoneyAndCount(User user, double total ) {
        return commonDao.reduceMoneyAndCount(user, total);
    }

    @Override
    public void reduceCount(Integer goodsId, int number) {
        commonDao.reduceCount(goodsId,number);
    }

    @Override
    public void returnBalance(Integer userId,Double total) {
        commonDao.returnBalance(userId,total);
    }


}
