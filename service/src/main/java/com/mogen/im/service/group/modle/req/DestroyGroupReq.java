package com.mogen.im.service.group.modle.req;

import com.mogen.im.common.model.RequestBase;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DestroyGroupReq extends RequestBase {

    @NotNull(message = "group Id can't not be null")
    private Integer groupId;

}
