package com.wangqi.entity;

import com.wangqi.pojo.Bill;
import com.wangqi.pojo.Store;
import com.wangqi.pojo.User;
import lombok.Data;

import java.util.List;
@Data
public class PayResult {
    private String orderId;
    private List<Bill> list;
    private User user;
    private Double total;
    private Store store;

}
