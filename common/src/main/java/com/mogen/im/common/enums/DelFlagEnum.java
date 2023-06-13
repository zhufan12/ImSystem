package com.mogen.im.common.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum DelFlagEnum  {

    /**
     * 0 normal；1 deleted。
     */
    NORMAL,

    DELETE;



    DelFlagEnum(){

    }


    @JsonValue
    public int toValue() {
        return ordinal();
    }

}
