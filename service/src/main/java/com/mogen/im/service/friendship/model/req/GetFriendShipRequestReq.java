package com.mogen.im.service.friendship.model.req;

import com.mogen.im.common.model.RequestBase;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class GetFriendShipRequestReq extends RequestBase {

    @NotBlank(message = "fromId can't not be null")
    private String fromId;

}
