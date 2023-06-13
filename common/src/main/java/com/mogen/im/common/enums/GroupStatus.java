package com.mogen.im.common.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum GroupStatus {

    NORMAL,

    DESTROY,

    ;


    GroupStatus(){
    }


    @JsonValue
    public int toValue() {
        return ordinal();
    }

}
