package com.mogen.im.service.group.modle.req;

import com.mogen.im.common.enums.GroupType;
import com.mogen.im.common.model.RequestBase;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class GetJoinedGroupReq extends RequestBase {

    @NotBlank(message = "user Id can't be null")
    private String userId;

    private List<GroupType> groupType;

    private Integer limit;

    private Integer offset;

}
