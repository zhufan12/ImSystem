package com.mogen.im.common.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum FriendShipStatusEnum {
    FRIEND_STATUS_NO_FRIEND,
    FRIEND_STATUS_NORMAL,
    FRIEND_STATUS_DELETE,

    BLACK_STATUS_NORMAL,

    BLACK_STATUS_BLACKED;

    FriendShipStatusEnum(){

    }


    @JsonValue
    public int toValue() {
        return ordinal();
    }




}
