package com.mogen.im.common.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum AllowFriendType {


    NEED,


    NOT_NEED;



    AllowFriendType(){

    }

    @JsonValue
    public int toValue() {
        return ordinal();
    }



}
