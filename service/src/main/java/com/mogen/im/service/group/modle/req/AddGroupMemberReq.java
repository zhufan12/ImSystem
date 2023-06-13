package com.mogen.im.service.group.modle.req;

import com.mogen.im.common.model.RequestBase;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class AddGroupMemberReq extends RequestBase {

    @NotNull(message = "group id can't be null")
    private Integer groupId;

    @NotEmpty(message = "group members can't be null")
    private List<GroupMemberDto> members;

}
