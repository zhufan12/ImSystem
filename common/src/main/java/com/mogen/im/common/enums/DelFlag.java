package com.mogen.im.common.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum DelFlag {

    /**
     * 0 normal；1 deleted。
     */
    NORMAL,

    DELETE;



    DelFlag(){

    }


    @JsonValue
    public int toValue() {
        return ordinal();
    }

}
