package com.mogen.im.common.enums;

public enum DeviceMultiLoginModel {

    ONE(1,"DeviceMultiLoginEnum_ONE"),

    TWO(2,"DeviceMultiLoginEnum_TWO"),

    THREE(3,"DeviceMultiLoginEnum_THREE"),

    ALL(4,"DeviceMultiLoginEnum_ALL");

    private int loginMode;
    private String loginDesc;


    public static DeviceMultiLoginModel getMember(int ordinal) {
        for (int i = 0; i < DeviceMultiLoginModel.values().length; i++) {
            if (DeviceMultiLoginModel.values()[i].getLoginMode() == ordinal) {
                return DeviceMultiLoginModel.values()[i];
            }
        }
        return THREE;
    }

    DeviceMultiLoginModel(int loginMode, String loginDesc){
        this.loginMode=loginMode;
        this.loginDesc=loginDesc;
    }

    public int getLoginMode() {
        return loginMode;
    }

    public void setLoginMode(int loginMode) {
        this.loginMode = loginMode;
    }

    public String getLoginDesc() {
        return loginDesc;
    }

    public void setLoginDesc(String loginDesc) {
        this.loginDesc = loginDesc;
    }

}
