package com.mogen.im.common;

import com.mogen.im.common.exception.ApplicationExceptionEnum;

public enum ResponseCode implements ApplicationExceptionEnum {


    SUCCESSD(200,"success"),
    SYSTEM_ERROR(90000,"system error please contact the administrator"),
    PARAMETER_ERROR(90001,"input parameter error");

    ResponseCode(int code, String error){
        this.code = code;
        this.msg = error;
    }

    private int code;

    private String msg;

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getMsg() {
        return msg;
    }
}
