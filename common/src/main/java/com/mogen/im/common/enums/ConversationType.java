package com.mogen.im.common.enums;


import com.fasterxml.jackson.annotation.JsonValue;

public enum ConversationType {

    P_TO_P,
    GROUP;

    ConversationType(){

    }

    @JsonValue
    public int getValue(){
        return ordinal();
    }
}
