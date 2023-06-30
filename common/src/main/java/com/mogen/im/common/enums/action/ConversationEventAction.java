package com.mogen.im.common.enums.action;

public enum ConversationEventAction implements Action {

    CONVERSATION_DELETE(5000),

    //删除会话
    CONVERSATION_UPDATE(5001),

    ;

    private int action;

    ConversationEventAction(int action){
        this.action = action;
    }


    @Override
    public int getAction() {
        return action;
    }
}
