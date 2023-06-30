package com.mogen.im.common.model.message;

import com.mogen.im.common.model.ClientInfo;
import lombok.Data;

@Data
public class MessageReadedContent extends ClientInfo {

    private long messageSequence;

    private String fromId;

    private Integer groupId;

    private String toId;

    private Integer conversationType;

}
