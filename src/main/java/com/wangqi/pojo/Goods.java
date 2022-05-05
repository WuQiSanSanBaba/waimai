package com.wangqi.pojo;

import lombok.Data;

@Data
public class Goods {
  private Integer id;
  private String name;
  private String type;
  private Integer count;
  private Double price;
  private Integer ison;
  private String imgaddr;
  private String imgname;
  private String description;
}
