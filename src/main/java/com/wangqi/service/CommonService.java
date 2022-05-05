package com.wangqi.service;

import com.wangqi.pojo.Store;
import com.wangqi.pojo.User;

import java.util.List;

public interface CommonService {
    List<String> getMessage();

    List<Store> initCarousel(int count);

    List<Store> getAllStore();


    boolean reduceMoneyAndCount(User user, double total);

    void reduceCount(Integer goodsId, int number);

    void returnBalance(Integer userId,Double total);
}
