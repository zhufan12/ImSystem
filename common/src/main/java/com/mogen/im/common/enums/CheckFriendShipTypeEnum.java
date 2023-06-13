package com.mogen.im.common.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum CheckFriendShipTypeEnum {

    SINGLE,

    BOTH;


    CheckFriendShipTypeEnum(){

    }

    @JsonValue
    public int toValue() {
        return ordinal();
    }

}
