package com.wangqi.dao;

import com.wangqi.pojo.Store;
import com.wangqi.pojo.User;

import java.util.List;
//实现类有更为详细的注释
public interface CommonDao {
    //获取消息
    List<String> getMessage();
    //初始化走马灯
    List<Store> initCarousel(int count);
    //获取所有店铺
    List<Store> getAllStore();
    //下单后减少用户余额
    boolean reduceMoneyAndCount(User user, double total );
    //减少商品库存
    void reduceCount(Integer goodsId, int number);
    //取消订单时候退钱给用户
    void returnBalance(Integer userId,Double total);

}
