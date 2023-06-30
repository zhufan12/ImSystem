package com.mogen.im.common.enums;

import com.mogen.im.common.exception.ApplicationExceptionEnum;

public enum MessageErrorCode implements ApplicationExceptionEnum {

    FORMER_IS_MUTE(50002,"former is mute"),

    FORMER_IS_FORBIDDEN(50003,"former is forbidden"),


    MESSAGE_BODY_IS_NOT_EXIST(50003,"message body not"),

    MESSAGE_RECALL_TIME_OUT(50004,"message recall time out"),

    MESSAGE_IS_RECALLED(50005,"message is recalled");


    private int code;

    private String msg;

    MessageErrorCode(int code,String msg){
        this.code = code;
        this.msg = msg;
    }


    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getMsg() {
        return msg;
    }
}
