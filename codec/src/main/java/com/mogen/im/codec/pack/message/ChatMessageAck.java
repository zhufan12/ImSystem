package com.mogen.im.codec.pack.message;

import lombok.Data;

@Data
public class ChatMessageAck {

    private String messageId;

    private Long messageSequence;


    public ChatMessageAck(){

    }

    public ChatMessageAck(String messageId,Long messageSequence) {
        this.messageId = messageId;
        this.messageSequence = messageSequence;
    }
}
