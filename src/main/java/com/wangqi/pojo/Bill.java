package com.wangqi.pojo;

import lombok.Data;

import java.io.Serializable;

@Data
public class Bill implements Serializable {
  private   String name;//商品名称
  private  int number;//商品数量
  private  double subtotal;//商品小计 商品数量*商品价格
  private String orderId; //订单号
  private Integer goodsId;//商品id

}
