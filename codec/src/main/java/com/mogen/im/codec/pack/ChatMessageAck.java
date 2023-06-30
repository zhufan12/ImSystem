package com.mogen.im.codec.pack;

import lombok.Data;

@Data
public class ChatMessageAck {

    private String messageId;

    public ChatMessageAck(String messageId) {
        this.messageId = messageId;
    }
}
