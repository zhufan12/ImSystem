package com.mogen.im.common.enums;

import com.mogen.im.common.exception.ApplicationExceptionEnum;

public enum UserErrorCode implements ApplicationExceptionEnum {


    IMPORT_SIZE_BEYOND(20000, "import user over the max number"),
    USER_IS_NOT_EXIST(20001, "user not exist"),
    SERVER_GET_USER_ERROR(20002, "get user info error"),
    MODIFY_USER_ERROR(20003, "update user info failed"),
    SERVER_NOT_AVAILABLE(71000, "not available server");

    private int code;
    private String error;

    UserErrorCode(int code, String error) {
        this.code = code;
        this.error = error;
    }

    public int getCode() {
        return this.code;
    }

    public String getMsg() {
        return this.error;
    }
}