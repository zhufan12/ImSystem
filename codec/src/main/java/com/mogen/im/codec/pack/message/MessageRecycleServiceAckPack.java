package com.mogen.im.codec.pack.message;

import lombok.Data;

@Data
public class MessageRecycleServiceAckPack {


    private Long messageKey;

    private String fromId;

    private String toId;

    private Long messageSequence;

    private Boolean serverSend;
}
