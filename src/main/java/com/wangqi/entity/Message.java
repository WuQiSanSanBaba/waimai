package com.wangqi.entity;

import lombok.Data;
//对应数据库 messagecentre
@Data
public class Message  {
   private String message;
   private Integer id;
   private Integer hide;//1代表开启 0代表隐藏
}
