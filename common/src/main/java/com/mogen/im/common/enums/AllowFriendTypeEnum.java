package com.mogen.im.common.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum AllowFriendTypeEnum {


    NEED,


    NOT_NEED;



    AllowFriendTypeEnum(){

    }

    @JsonValue
    public int toValue() {
        return ordinal();
    }



}
