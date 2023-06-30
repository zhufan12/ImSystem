package com.mogen.im.service.message.model.req;

import lombok.Data;

@Data
public class CheckSendMessageReq  {

    private String fromId;

    private String toId;

    private Integer appId;

    private Integer command;

}
