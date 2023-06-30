package com.mogen.im.common.enums.action;

public enum FriendshipEventAction implements Action{

    FRIEND_ADD(3000),

    FRIEND_UPDATE(3001),

    FRIEND_DELETE(3002),

    FRIEND_REQUEST(3003),

    FRIEND_REQUEST_READ(3004),

    FRIEND_REQUEST_APPROVER(3005),
    FRIEND_BLACK_ADD(3010),

    FRIEND_GROUP_ADD(3012),

    FRIEND_GROUP_DELETE(3013),

    FRIEND_GROUP_MEMBER_ADD(3014),

    FRIEND_GROUP_MEMBER_DELETE(3015),

    FRIEND_ALL_DELETE(3016);

    private int action;

    FriendshipEventAction(int action){
        this.action = action;
    }


    @Override
    public int getAction() {
        return action;
    }
}
