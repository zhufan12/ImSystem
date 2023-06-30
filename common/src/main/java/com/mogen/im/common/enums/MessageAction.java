package com.mogen.im.common.enums;

import com.mogen.im.common.enums.action.Action;

public enum MessageAction implements Action {
    //1103
    MSG_P2P(0x44F),

    // 1046
    MSG_ACK(0x416),

    // 1107
    MSG_RECIVE_ACK(1107),

    // 1106
    MSG_READED(0x452),

    // 1053
    MSG_READED_NOTIFY(0x41D),

    // 1054
    MSG_READED_RECEIPT(0x41E),

    // 1050
    MSG_RECALL(0x41A),

    // 1052
    MSG_RECALL_NOTIFY(0x41C),

    // 1051
    MSG_RECALL_ACK(0x41B),
    ;

    MessageAction(Integer action){
        this.action = action;
    }

    private Integer action;

    @Override
    public int getAction() {
        return action;
    }
}
