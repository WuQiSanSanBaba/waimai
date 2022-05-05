package com.wangqi.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
@Data
@AllArgsConstructor
public class PageResult {
   private Integer total;
   private List list;
}
