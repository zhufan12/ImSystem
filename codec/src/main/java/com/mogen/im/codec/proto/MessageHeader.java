package com.mogen.im.codec.proto;

import lombok.Data;

@Data
public class MessageHeader{

    private Integer action;

    private Integer version;

    private Integer clientType;

    private Integer appId;

    // json : 0x0 , protoByte 0x1 , xml 0x2,
    private Integer payloadType = 0x0;


    private Integer imeiLength;


    private int payloadLength;


    private String imei;

}