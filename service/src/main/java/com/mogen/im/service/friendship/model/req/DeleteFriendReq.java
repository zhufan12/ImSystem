package com.mogen.im.service.friendship.model.req;

import com.mogen.im.common.model.RequestBase;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DeleteFriendReq extends RequestBase {

    @NotBlank(message = "fromId could not be null")
    private String fromId;

    @NotBlank(message = "toId could not be null")
    private String toId;

}
