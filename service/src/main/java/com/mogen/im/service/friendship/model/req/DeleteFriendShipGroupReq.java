package com.mogen.im.service.friendship.model.req;

import com.mogen.im.common.model.RequestBase;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class DeleteFriendShipGroupReq extends RequestBase {

    @NotBlank(message = "fromId could not be null")
    private String fromId;

    @NotEmpty(message = "group name can't be empty")
    private List<String> groupName;

}