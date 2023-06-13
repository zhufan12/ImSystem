package com.mogen.im.service.group.modle.req;

import com.mogen.im.common.enums.GroupMemberRole;
import com.mogen.im.common.model.RequestBase;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateGroupMemberReq extends RequestBase {

    @NotNull(message = " group id can't be null")
    private Integer groupId;

    @NotBlank(message = "memberId can't be null")
    private String userId;

    private String alias;

    private GroupMemberRole role;

    private String extra;

}
