package com.mogen.im.service.user.model.req;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LoginReq {

    @NotNull(message = "user pid can't be null")
    private String userPid;

    @NotNull(message = "app id can't not be null")
    private Integer appId;

    private Integer clientType;
}
