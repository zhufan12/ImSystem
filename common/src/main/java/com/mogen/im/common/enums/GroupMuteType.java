package com.mogen.im.common.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum GroupMuteType {

    NOT_MUTE,


    MUTE,

    ;

    GroupMuteType(){

    }

    @JsonValue
    public int toValue() {
        return ordinal();
    }

}
