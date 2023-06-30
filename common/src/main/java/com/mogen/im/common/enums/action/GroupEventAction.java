package com.mogen.im.common.enums.action;

public enum GroupEventAction implements Action{

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

    //  2106
    MSG_GROUP_READED(0x83a),

    //2053
    MSG_GROUP_READED_NOTIFY(0x805),

    // 2054
    MSG_GROUP_READED_RECEIPT(0x806),

    // 2047
    GROUP_MSG_ACK(0x7ff),

    ;

    GroupEventAction(int action){
        this.action = action;
    }

    private int action;


    @Override
    public int getAction() {
        return action;
    }
}
