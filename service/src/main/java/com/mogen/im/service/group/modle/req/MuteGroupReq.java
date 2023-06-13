package com.mogen.im.service.group.modle.req;

import com.mogen.im.common.enums.GroupMuteType;
import com.mogen.im.common.model.RequestBase;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MuteGroupReq extends RequestBase {

    @NotNull(message = "groupId can't be null")
    private Integer groupId;

    @NotNull(message = "mute can't be null")
    private GroupMuteType mute;

}