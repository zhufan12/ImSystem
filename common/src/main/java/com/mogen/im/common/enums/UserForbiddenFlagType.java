package com.mogen.im.common.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum UserForbiddenFlagType {

    NORMAL,

    FORBIBBEN;


    UserForbiddenFlagType(){

    }

    @JsonValue
    public int toValue() {
        return ordinal();
    }
}
