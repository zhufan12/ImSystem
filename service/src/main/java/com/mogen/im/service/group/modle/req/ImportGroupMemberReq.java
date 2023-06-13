package com.mogen.im.service.group.modle.req;

import com.mogen.im.common.model.RequestBase;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class ImportGroupMemberReq extends RequestBase {

    @NotNull(message = "group Id cloud not be null")
    private Integer groupId;

    private List<GroupMemberDto> members;
}
