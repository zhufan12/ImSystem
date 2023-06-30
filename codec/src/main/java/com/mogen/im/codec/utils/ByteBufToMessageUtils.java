package com.mogen.im.codec.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mogen.im.codec.proto.Message;
import com.mogen.im.codec.proto.MessageHeader;
import io.netty.buffer.ByteBuf;


public class ByteBufToMessageUtils {

    private static ObjectMapper objectMapper;

    static {
        objectMapper = new ObjectMapper();
    }

    public static Message transition(ByteBuf in){

        int command = in.readInt();

        int version = in.readInt();

        int clientType = in.readInt();
        int messageType = in.readInt();

        int appId = in.readInt();
        int imeiLength = in.readInt();

        int bodyLen = in.readInt();

        if(in.readableBytes() < bodyLen + imeiLength){
            in.resetReaderIndex();
            return null;
        }

        byte [] imeiData = new byte[imeiLength];
        in.readBytes(imeiData);
        String imei = new String(imeiData);

        byte [] bodyData = new byte[bodyLen];
        in.readBytes(bodyData);


        MessageHeader messageHeader = new MessageHeader();
        messageHeader.setAppId(appId);
        messageHeader.setClientType(clientType);
        messageHeader.setAction(command);
        messageHeader.setPayloadLength(bodyLen);
        messageHeader.setVersion(version);
        messageHeader.setPayloadType(messageType);
        messageHeader.setImei(imei);

        Message message = new Message();
        message.setMessageHeader(messageHeader);

        if(messageType == 0x0){
            String body = new String(bodyData);
            JsonNode jsonNode = null;
            try {
                jsonNode = objectMapper.readTree(body);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            message.setMessagePack(jsonNode);
        }

        in.markReaderIndex();
        return message;
    }

}
