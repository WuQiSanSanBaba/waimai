package com.wangqi.pojo;

import lombok.Data;

import java.util.List;

@Data
public class Order {
    private String id;
    private String userName;
    private String storeName;
    private String phonenumber;
    private int storeId;
    private int userId;
    private String date;
    private String addr;
    private double total;
    private String status;//已经下单，正在配送，配送完成
    private List<Bill> bill;
}
