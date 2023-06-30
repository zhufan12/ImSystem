package com.mogen.im.common.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum UserSilentFlagType {

    NORMAL,

    MUTE;

    UserSilentFlagType(){

    }


    @JsonValue
    public int toValue() {
        return ordinal();
    }

}
