package com.wangqi.service;

import com.wangqi.entity.Message;
import com.wangqi.entity.PageCondition;
import com.wangqi.entity.PageResult;
import com.wangqi.pojo.*;

import java.util.List;


public interface AdminService {

    Integer login(Admin admin);
    void editStore(Store store);
    void deletebyIdStore(Integer id);
    void changeOline(Integer state);

    Integer getIsOnlne();

    PageResult findByPageStore(PageCondition pg);

    void storeChangeOnline(Integer state,Integer id);

    int addStore(Store store);

    Store getbyIdStore(Integer id);


    List<Message> getMessage();

     void messageChange(Integer status, Integer id);

    void deleteMessaGebyid(Integer id);

    void addMessage(String  message);

    User getbyIdUser(Integer id);

    PageResult findByPageUser(PageCondition pg);

    void editUser(User user);

    void addUser(User user);

    void deletebyIdUser(Integer id);

    void recharge(Integer id, Double recharge);

}
