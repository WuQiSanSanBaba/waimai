package com.wangqi.service.impl;

import com.wangqi.dao.AdminDao;
import com.wangqi.entity.Message;
import com.wangqi.entity.PageCondition;
import com.wangqi.entity.PageResult;
import com.wangqi.pojo.*;
import com.wangqi.service.AdminService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminServiceImpl implements AdminService {
@Autowired
AdminDao adminDao;


    @Override
    public int addStore(Store store) {
        return adminDao.addStore(store);
    }




    @Override
    public PageResult findByPageStore(PageCondition pg) {
        PageResult byPage = adminDao.findByPageStore(pg);
        return byPage;
    }

    @Override
    public void storeChangeOnline(Integer state,Integer id) {
        adminDao.storeChangeOnline(state,id);
    }



    @Override
    public Integer login(Admin admin) {
      return   adminDao.login(admin);
    }

    @Override
    public Store getbyIdStore(Integer id) {
        return adminDao.getbyIdStore(id);
    }

    @Override
    public List<Message> getMessage() {
        return adminDao.getMessage();
    }

    @Override
    public void messageChange(Integer status, Integer id) {
        adminDao.messageChange(status,id);
    }

    @Override
    public void deleteMessaGebyid(Integer id) {
        adminDao.deleteMessaGebyid(id);
    }

    @Override
    public void addMessage(String  message ) {
        adminDao.addMessage(message);
    }

    @Override
    public User getbyIdUser(Integer id) {
        return adminDao.getbyIdUser(id);
    }
    @Override
    public PageResult findByPageUser(PageCondition pg) {
        return   adminDao.findByPageUser(pg);
    }

    @Override
    public void editUser(User user) {
        adminDao.editUser(user);
    }

    @Override
    public void addUser(User user) {
        adminDao.addUser(user);
    }

    @Override
    public void deletebyIdUser(Integer id) {
        adminDao.deletebyIdUser(id);
    }

    @Override
    public void recharge(Integer id, Double recharge) {
        adminDao.recharge(id,recharge);
    }

    @Override
    public void editStore(Store store) {
    adminDao.editStore(store);
    }




    @Override
    public void deletebyIdStore(Integer id) {
        adminDao.deletebyIdStore(id);
    }

    @Override
    public void changeOline(Integer state) {
        adminDao.changeOline(state);
    }

    @Override
    public Integer getIsOnlne() {
        return adminDao.getIsOnlne();
    }


}
