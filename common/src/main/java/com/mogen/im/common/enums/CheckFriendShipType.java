package com.mogen.im.common.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum CheckFriendShipType {

    SINGLE,

    BOTH;


    CheckFriendShipType(){

    }

    @JsonValue
    public int toValue() {
        return ordinal();
    }

}
