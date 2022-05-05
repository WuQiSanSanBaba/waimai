package com.wangqi.service;

import com.wangqi.entity.PageCondition;
import com.wangqi.entity.PageResult;
import com.wangqi.pojo.*;

import java.util.List;


public interface StoreService {

Store login(Store  store);

    Store getStoreInfo(Integer id);


    void editStore(Store store);


    PageResult findbyPageGoods(PageCondition cg, Integer id);

    void changeGoodsIson(Integer state, Integer id);

    void editGoods(Goods goods);

    Goods getbyIdGoods(Integer id);
    List<Goods> getAllGoods(Integer id);

    void addGoods(Goods goods,Integer id);

    void deletebyIdGoods(Integer id);

    void addRurnoverAndSaleCount(int storeId, double total);
}
