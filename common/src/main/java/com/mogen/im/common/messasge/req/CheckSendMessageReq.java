package com.mogen.im.common.messasge.req;

import lombok.Data;

@Data
public class CheckSendMessageReq {

    private String fromId;

    private String toId;

    private Integer appId;

    private Integer action;

}

