package com.mogen.im.service.friendship.model.req;

import com.mogen.im.common.model.RequestBase;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class AddFriendShipGroupReq extends RequestBase {

    @NotBlank(message = "fromId could not be null")
    private String fromId;

    @NotBlank(message = "group name can't be empty")
    private String groupName;

    private List<String> toIds;

}