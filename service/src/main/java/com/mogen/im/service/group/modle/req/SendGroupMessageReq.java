package com.mogen.im.service.group.modle.req;

import com.mogen.im.common.model.RequestBase;
import lombok.Data;

@Data
public class SendGroupMessageReq extends RequestBase {

    private String messageId;

    private String fromId;

    private Integer groupId;

    private int messageRandom;

    private long messageTime;

    private String messageBody;

    private int badgeMode;

    private Long messageLifeTime;

    private Integer appId;

}
