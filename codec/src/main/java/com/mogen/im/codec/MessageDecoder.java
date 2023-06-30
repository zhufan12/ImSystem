package com.mogen.im.codec;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mogen.im.codec.proto.Message;
import com.mogen.im.codec.proto.MessageHeader;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.Data;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Data
public class MessageDecoder extends ByteToMessageDecoder {

    // header 28 byte
    // header (Action,version,clientType,payloadType,appId,imeiLen,payloadLen)

    private ObjectMapper objectMapper;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {

        if(in.readableBytes() < 28){
            return;
        }

        int action = in.readInt();
        int version = in.readInt();
        int clientType = in.readInt();
        int payloadType = in.readInt();
        int appId = in.readInt();
        int imeiLen = in.readInt();
        int payloadLen = in.readInt();

        if(in.readableBytes() < (imeiLen + payloadLen)){
            in.resetReaderIndex();
            return;
        }

        byte[] imeiData = new byte[imeiLen];
        in.readBytes(imeiData);
        String imei = new String(imeiData);

        byte[] payloadData = new byte[payloadLen];
        in.readBytes(payloadData);
        String payload = new String(payloadData);

        //initial message header
        MessageHeader messageHeader = new MessageHeader();
        messageHeader.setAction(action);
        messageHeader.setAppId(appId);
        messageHeader.setClientType(clientType);
        messageHeader.setImei(imei);
        messageHeader.setPayloadType(payloadType);
        messageHeader.setPayloadLength(payloadLen);
        messageHeader.setImeiLength(imeiLen);
        messageHeader.setVersion(version);

        Message message = new Message();


        message.setMessageHeader(messageHeader);


        if(messageHeader.getPayloadType() == 0x2){

        }else if(messageHeader.getPayloadType() == 0x1){
            message.setMessagePack(payload.getBytes(StandardCharsets.UTF_8));
        }else{
            JsonNode jsonpObject = objectMapper.readTree(payload);
            message.setMessagePack(jsonpObject);
        }
        out.add(message);
        in.markReaderIndex();

    }
}
