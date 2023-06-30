package com.mogen.im.common.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ConnectStatus {

    ONLINE,

    OFFLIN;


    ConnectStatus() {

    }
    @JsonValue
    public int toValue() {
        return ordinal();
    }


}
