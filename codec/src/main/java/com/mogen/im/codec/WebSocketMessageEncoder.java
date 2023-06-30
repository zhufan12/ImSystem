package com.mogen.im.codec;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.mogen.im.codec.proto.MessagePack;
import com.mogen.im.codec.utils.JsonUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class WebSocketMessageEncoder extends MessageToMessageEncoder<MessagePack> {

    private static ObjectMapper objectMapper = JsonUtils.getInstance();

    private static Logger log = LoggerFactory.getLogger(WebSocketMessageEncoder.class);

    @Override
    protected void encode(ChannelHandlerContext ctx, MessagePack msg, List<Object> out)  {

        try {
            String s = objectMapper.writeValueAsString(msg);
            ByteBuf byteBuf = Unpooled.directBuffer(8+s.length());
            byte[] bytes = s.getBytes();
            byteBuf.writeInt(msg.getAction());
            byteBuf.writeInt(bytes.length);
            byteBuf.writeBytes(bytes);
            out.add(new BinaryWebSocketFrame(byteBuf));
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}