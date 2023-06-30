package com.mogen.im.service.friendship.model.req;

import com.mogen.im.common.enums.CheckFriendShipType;
import com.mogen.im.common.model.RequestBase;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class CheckFriendShipReq extends RequestBase {

    @NotBlank(message = "fromId can't not be null")
    private String fromId;

    @NotEmpty(message = "toIds can't not be null")
    private List<String> toIds;

    @NotNull(message = "checkType can't not be null")
    private CheckFriendShipType checkType;
}
