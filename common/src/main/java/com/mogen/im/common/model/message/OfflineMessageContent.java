package com.mogen.im.common.model.message;

import lombok.Data;

@Data
public class OfflineMessageContent {

    private Integer appId;

    private Long messageKey;

    private String messageBody;

    private Long messageTime;

    private String extra;

    private Integer delFlag;

    private String fromId;

    private String toId;

    private Long messageSequence;

    private String messageRandom;

    private Integer conversationType;

    private String conversationId;
}
