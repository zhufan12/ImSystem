package com.mogen.im.codec;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mogen.im.codec.proto.MessagePack;
import com.mogen.im.codec.utils.JsonUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class MessageEncoder extends MessageToByteEncoder {

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        if (msg instanceof MessagePack) {
            ObjectMapper mapper = JsonUtils.getInstance();
            MessagePack msgBody = (MessagePack) msg;
            String s = mapper.writeValueAsString(msg);
            byte[] bytes = s.getBytes();
            out.writeInt(msgBody.getAction());
            out.writeInt(bytes.length);
            out.writeBytes(bytes);
        }
    }
}