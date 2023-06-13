package com.mogen.im.common.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum GroupType {

    PRIVATE,

    PUBLIC;



    GroupType(){

    }

    @JsonValue
    public int toValue() {
        return ordinal();
    }

}
