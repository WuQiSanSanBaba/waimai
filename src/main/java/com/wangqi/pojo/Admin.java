package com.wangqi.pojo;

import lombok.Data;
//用于接收账号密码的json数据 同时应用于接收商家登录和管理员登录
@Data
public class Admin {
    private String username;
    private String password;


}
