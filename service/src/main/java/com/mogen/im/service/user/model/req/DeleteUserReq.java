package com.mogen.im.service.user.model.req;

import com.mogen.im.common.model.RequestBase;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class DeleteUserReq extends RequestBase {

    @NotEmpty(message = "user pid can't be null")
    private List<String> userPids;
}
