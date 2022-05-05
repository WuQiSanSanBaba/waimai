package com.wangqi.entity;

import lombok.Data;

@Data
public class Result {
    private boolean flag;
    private String message;
    private Object Data;

    public Result(boolean aBoolean, String message) {
        this.flag = aBoolean;
        this.message = message;
    }

    public Result(boolean aBoolean, String message, Object Data) {
        this.flag = aBoolean;
        this.message = message;
        this.Data = Data;
    }
}
