package com.mogen.im.service.group.modle.req;

import com.mogen.im.common.model.RequestBase;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TransferGroupReq  extends RequestBase {

    @NotNull(message = "group id can't be null")
    private Integer groupId;

    private String ownerId;

}