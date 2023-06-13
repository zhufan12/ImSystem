package com.mogen.im.service.user.model.req;

import com.mogen.im.common.model.RequestBase;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class ModifyUserInfoReq extends RequestBase {

    @NotEmpty(message = "user pid cloud not be empty")
    private String userPid;

    private String nickName;

    private String location;

    private String birthDay;

    private String password;

    private String photo;

    private String userSex;

    private String selfSignature;

    private Integer friendAllowType;

    private String extra;


}