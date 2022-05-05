package com.wangqi.dao;


import com.wangqi.entity.PageCondition;
import com.wangqi.entity.PageResult;
import com.wangqi.pojo.*;

import java.util.List;
//实现类有更为详细注释
public interface StoreDao {
    //店铺登录
    Store login(Store store);
    //获取店铺信息
    Store getStoreInfo(Integer id);
    //编辑店铺
    void editStore(Store store);

    //获取商品列表
    PageResult findbyPageGoods(PageCondition cg, Integer id);

    //获取所有的商品
    List<Goods> getAllGoods(Integer id);
    //获取店铺上线状态
    void goodSison(Integer state, Integer id);
    //编辑商品
    void editGoods(Goods goods);
    //通过ID获取商品
    Goods getbyIdGoods(Integer id);
    //添加商品
    void addGoods(Goods goods,Integer id);
    //通过ID删除商品
    void deletebyIdGoods(Integer id);
    //完成订单给商家增加销售额+total 和销售量+1
    void addRurnoverAndSaleCount(int storeId, double total);
}
