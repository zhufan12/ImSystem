package com.mogen.im.service.friendship.model.req;

import com.mogen.im.common.model.RequestBase;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class GetRelationReq extends RequestBase {

    @NotBlank(message = "from id can't not be null")
    private String fromId;

    @NotBlank(message = "toId could not be null")
    private String toId;

}
