package com.mogen.im.service.user.model.req;

import com.mogen.im.common.model.RequestBase;
import com.mogen.im.service.user.entity.User;
import com.mogen.im.service.user.service.impl.UserServiceImpl;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class ImportUserReq  extends RequestBase {

    @NotNull(message = "user data can't be null")
    private List<User> userdata;



}
