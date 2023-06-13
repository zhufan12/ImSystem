package com.mogen.im.service.friendship.model.req;

import com.mogen.im.common.model.RequestBase;
import lombok.Data;

@Data
public class ApprovedFriendRequestReq extends RequestBase {

    private Integer id;

    private Integer status;
}