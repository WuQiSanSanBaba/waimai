package com.wangqi.dao;


import com.wangqi.entity.Message;
import com.wangqi.entity.PageCondition;
import com.wangqi.entity.PageResult;
import com.wangqi.pojo.*;

import java.util.List;
//在实现类中有更为详细的注释
public interface AdminDao {
    //登录
    Integer login(Admin admin);
    //通过ID获取商店信息
    Store getbyIdStore(Integer id);
    //通过传入的store类修改商店数据
    void editStore(Store store);
    //通过获取到商店ID删除商店
    void deletebyIdStore(Integer id);
    //更改平台在线状态
    void changeOline(Integer state);
    //过去平台状态
    Integer getIsOnlne();
    //给用户充值 ID对应用户ID  recharge是充值金额
    void recharge(Integer id, Double recharge);
    //商店分页查询
    PageResult findByPageStore(PageCondition pg);
    //更改店铺的上线状态 id是商店ID state是是否上线 1 是上线 0是下线
    void storeChangeOnline(Integer state, Integer id);
    //根据store获取各项信息添加商店
    int addStore(Store store);
    //获取所有消息
    List<Message> getMessage();
    //通过消息ID更改消息状态
    void messageChange(Integer status, Integer id);
    //通过消息ID删除消息
    void deleteMessaGebyid(Integer id);
    //增加消息
    void addMessage(String message);
    //根据ID获取用户
    User getbyIdUser(Integer id);
    //返回分页结果
    PageResult findByPageUser(PageCondition pg);
    //编辑用户
    void editUser(User user);
    //添加用户
    void addUser(User user);
    //通过ID删除用户
    void deletebyIdUser(Integer id);

}
