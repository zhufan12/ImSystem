package com.mogen.im.common.enums;

import com.mogen.im.common.enums.action.Action;

public enum GroupEventAction implements Action {

    JOIN_GROUP(2000),

    ADDED_MEMBER(2001),

    CREATED_GROUP(2002),
    UPDATED_GROUP(2003),

    EXIT_GROUP(2004),
    UPDATED_MEMBER(2005),
    DELETED_MEMBER(2006),
    DESTROY_GROUP(2007),

    TRANSFER_GROUP(2008),

    MUTE_GROUP(2009),

    SPEAK_GOUP_MEMBER(2010),

    // 2104
    MSG_GROUP(0x838),
    MSG_GROUP_READED(0x83a),

    MSG_GROUP_READED_NOTIFY(0x805),

    MSG_GROUP_READED_RECEIPT(0x806),

    GROUP_MSG_ACK(0x7ff);

    GroupEventAction(Integer action){
        this.action = action;
    }

    private Integer action;


    @Override
    public int getAction() {
        return action;
    }
}
