package com.mogen.im.common.enums;


import com.mogen.im.common.exception.ApplicationExceptionEnum;


public enum GroupErrorCode implements ApplicationExceptionEnum {

    GROUP_IS_NOT_EXIST(40000,"group not exist"),

    GROUP_IS_EXIST(40001,"group already exist"),

    GROUP_IS_HAVE_OWNER(40002,"group already exist group owner"),

    USER_IS_JOINED_GROUP(40003,"user is joined group"),

    USER_JOIN_GROUP_ERROR(40004,"group member add failed"),

    GROUP_MEMBER_IS_BEYOND(40005,"group member over the max"),

    MEMBER_IS_NOT_JOINED_GROUP(40006,"user not exist group"),

    THIS_OPERATE_NEED_MANAGER_ROLE(40007,"This operation is only allowed for group owners/admins"),

    THIS_OPERATE_NEED_APPMANAGER_ROLE(40008,"This operation is only allowed for APP administrators"),

    THIS_OPERATE_NEED_OWNER_ROLE(40009,"This operation is only allowed by the group owner"),

    GROUP_OWNER_IS_NOT_REMOVE(40010,"can't not remover the group owner"),

    UPDATE_GROUP_BASE_INFO_ERROR(40011,"update group info error"),

    THIS_GROUP_IS_MUTE(40012,"group is mute"),

    IMPORT_GROUP_ERROR(40013,"import group failed"),

    THIS_OPERATE_NEED_ONESELF(40014,"This operation is only allowed for yourself"),

    PRIVATE_GROUP_CAN_NOT_DESTROY(40015,"private can't be destroy"),

    PUBLIC_GROUP_MUST_HAVE_OWNER(40016,"public group must set the group owner"),

    GROUP_MEMBER_IS_SPEAK(40017,"group member is speak"),

    GROUP_IS_DESTROY(40018,"group already destroy"),

    ;

    private int code;
    private String msg;

    GroupErrorCode(int code, String msg){
        this.code = code;
        this.msg = msg;
    }
    public int getCode() {
        return this.code;
    }

    @Override
    public String getMsg() {
        return this.msg;
    }



}
