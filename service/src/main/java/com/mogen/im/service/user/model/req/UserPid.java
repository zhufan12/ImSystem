package com.mogen.im.service.user.model.req;

import com.mogen.im.common.model.RequestBase;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserPid extends RequestBase {

    @NotNull(message = "user Pid con't be null")
    private String userPid;
}
