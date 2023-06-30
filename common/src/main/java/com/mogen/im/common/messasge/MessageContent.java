package com.mogen.im.common.messasge;

import com.mogen.im.common.model.ClientInfo;
import lombok.Data;

@Data
public class MessageContent extends ClientInfo {

    private String messageId;

    private String fromId;

    private String toId;

    private String messageBody;

    private Long messageTime;

    private String extra;

    private Long messageKey;

    private Long messageSequence;


}
