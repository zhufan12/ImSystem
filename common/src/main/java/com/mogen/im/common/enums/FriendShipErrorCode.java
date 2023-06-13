package com.mogen.im.common.enums;

import com.mogen.im.common.exception.ApplicationExceptionEnum;

public enum FriendShipErrorCode implements ApplicationExceptionEnum {


    IMPORT_SIZE_BEYOND(30000,"over the import max number"),

    ADD_FRIEND_ERROR(30001,"add friend failed"),

    TO_IS_YOUR_FRIEND(30002,"He/she is already  your friend"),

    TO_IS_NOT_YOUR_FRIEND(30003,"He/she is not your friend"),

    FRIEND_IS_DELETED(30004,"friend is deleted"),

    FRIEND_IS_BLACK(30006,"friend is blacklisted "),

    TARGET_IS_BLACK_YOU(30007,"your be blacklisted "),

    REPEATSHIP_IS_NOT_EXIST(30008,"relationship not exist "),

    ADD_BLACK_ERROR(30009,"blacklisted failed"),

    FRIEND_IS_NOT_YOUR_BLACK(30010,"friend is move out the blacklist"),

    NOT_APPROVE_OTHER_MAN_REQUEST(30011,"could not approve other friend's request "),

    FRIEND_REQUEST_IS_NOT_EXIST(30012,"friend request not exist"),

    FRIEND_SHIP_GROUP_CREATE_ERROR(30014,"create friend group failed"),

    FRIEND_SHIP_GROUP_IS_EXIST(30015,"friend group already existed"),

    FRIEND_SHIP_GROUP_IS_NOT_EXIST(30016,"friend group not exist"),



    ;

    private int code;
    private String error;

    FriendShipErrorCode(int code, String error){
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
