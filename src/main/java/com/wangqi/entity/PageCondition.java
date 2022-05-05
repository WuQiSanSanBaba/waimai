package com.wangqi.entity;

import lombok.Data;


/**
 * 用于封装分页查询条件
 * currentPage 当前页面
 * pageSize 页面大小
 * qurry  查询条件
 */
@Data
public class PageCondition {
   private Integer currentPage;
   private Integer pageSize;
   private String  query;
}
