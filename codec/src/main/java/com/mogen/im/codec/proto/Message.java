package com.mogen.im.codec.proto;

import com.mogen.im.codec.proto.MessageHeader;
import lombok.Data;

@Data
public class Message {

    private MessageHeader messageHeader;

    private Object messagePack;

    @Override
    public String toString() {
        return "Message{" +
                "messageHeader=" + messageHeader +
                ", messagePack=" + messagePack +
                '}';
    }
}
