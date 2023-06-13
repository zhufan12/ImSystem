package com.mogen.im.service.group.modle.req;

import com.mogen.im.common.model.RequestBase;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ExitGroupReq extends RequestBase {

    @NotNull(message = "group Id can't be null")
    private Integer groupId;

    private String memberId;

}
