package com.mogen.im.common.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum GroupMemberRole {
    ORDINARY,
    MANAGER,
    OWNER,
    LEAVE;
    ;




    GroupMemberRole(){

    }

    @JsonValue
    public int toValue() {
        return ordinal();
    }

}
