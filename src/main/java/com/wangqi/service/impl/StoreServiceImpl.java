package com.wangqi.service.impl;

import com.wangqi.dao.StoreDao;
import com.wangqi.entity.PageCondition;
import com.wangqi.entity.PageResult;
import com.wangqi.pojo.*;
import com.wangqi.service.StoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StoreServiceImpl implements StoreService {
@Autowired
StoreDao dao;



    @Override
    public Store login(Store store) {
      return   dao.login(store);
    }

    @Override
    public Store getStoreInfo(Integer id) {
        return dao.getStoreInfo(id);
    }

    @Override
    public void editStore(Store store) {
        dao.editStore(store);
    }



    @Override
    public PageResult findbyPageGoods(PageCondition cg, Integer id) {
        return dao.findbyPageGoods(cg,id);
    }

    @Override
    public void changeGoodsIson(Integer state, Integer id) {
        dao.goodSison(state,id);
    }

    @Override
    public void editGoods(Goods goods) {
        dao.editGoods(goods);
    }

    @Override
    public Goods getbyIdGoods(Integer id) {
        return dao.getbyIdGoods(id);
    }

    @Override
    public List<Goods> getAllGoods(Integer id) {
        return dao.getAllGoods(id);
    }

    @Override
    public void addGoods(Goods goods,Integer id) {
        dao.addGoods(goods,id);
    }

    @Override
    public void deletebyIdGoods(Integer id) {
        dao.deletebyIdGoods(id);
    }

    @Override
    public void addRurnoverAndSaleCount(int storeId, double total) {
        dao.addRurnoverAndSaleCount(storeId,total);
    }
}
