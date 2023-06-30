package com.mogen.im.common.enums.action;

public enum UserEventAction implements Action {
    USER_MODIFY(4000),


    USER_ONLINE_STATUS_CHANGE(4001),



    USER_ONLINE_STATUS_CHANGE_NOTIFY(4004),


    USER_ONLINE_STATUS_CHANGE_NOTIFY_SYNC(4005);

    private int action;

    UserEventAction(int action){
        this.action = action;
    }


    @Override
    public int getAction() {
        return action;
    }
}
