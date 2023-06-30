package com.mogen.im.common.enums;

import com.mogen.im.common.exception.ApplicationExceptionEnum;

public enum GateWayErrorCode implements ApplicationExceptionEnum {

    USERSIGN_NOT_EXIST(60000,"user sign not exist"),

    APPID_NOT_EXIST(60001,"appId not exist"),

    OPERATER_NOT_EXIST(60002," operater  not exist "),

    USERSIGN_IS_ERROR(60003,"user sign is Incorrect"),

    USERSIGN_OPERATE_NOT_MATE(60005,"user sign and  operater not match "),

    USERSIGN_IS_EXPIRED(60004,"user sign is expired"),

            ;
    GateWayErrorCode(int code, String msg){
        this.code = code;
        this.msg = msg;
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
