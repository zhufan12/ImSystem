package com.mogen.im.tcp.feign;

import com.mogen.im.common.ResponseVo;
import com.mogen.im.common.messasge.req.CheckSendMessageReq;
import feign.Headers;
import feign.RequestLine;

public interface FeignMessageService {


    @Headers({"Content-Type: application/json","Accept: application/json"})
    @RequestLine("POST /message/checkSend")
    public ResponseVo checkSendMessage(CheckSendMessageReq checkSendMessageReq);
}
