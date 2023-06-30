package com.mogen.im.service.message.model.req;

import com.mogen.im.common.model.RequestBase;
import lombok.Data;

@Data
public class SendMessageReq extends RequestBase {


    private String messageId;

    private String fromId;

    private String toId;

    private int messageRandom;

    private long messageTime;

    private String messageBody;

    private int badgeMode;

    private Long messageLifeTime;

    private Integer appId;

}
