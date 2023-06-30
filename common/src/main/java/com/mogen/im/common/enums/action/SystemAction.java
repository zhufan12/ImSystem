package com.mogen.im.common.enums.action;

public enum SystemAction implements Action {

    // 9999
    PING(0x270f),

    // 9000
    LOGIN(0x2328),

    // 9003
    LOGINACK(0x2329),

    LOGOUT(0x232b),

    MUTUALLOGIN(0x232a),

    ;

    private int action;

    SystemAction(int action){
        this.action = action;
    }


    @Override
    public int getAction() {
        return action;
    }
}
