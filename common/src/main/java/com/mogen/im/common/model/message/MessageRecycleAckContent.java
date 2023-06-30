package com.mogen.im.common.model.message;

import com.mogen.im.common.model.ClientInfo;
import lombok.Data;

@Data
public class MessageRecycleAckContent extends ClientInfo {

    private Long messageKey;

    private String fromId;

    private String toId;

    private Long messageSequence;

}
