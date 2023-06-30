package com.mogen.im.service.message.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mogen.im.common.ResponseVo;
import com.mogen.im.common.model.SyncReq;
import com.mogen.im.service.message.model.req.CheckSendMessageReq;
import com.mogen.im.service.message.model.req.SendMessageReq;
import com.mogen.im.service.message.service.MessageSyncService;
import com.mogen.im.service.message.service.PToPMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("v1/message")
public class MessageController {


    @Autowired
    private PToPMessageService pToPMessageService;

    @Autowired
    private MessageSyncService messageSyncService;


    @RequestMapping("/send")
    public ResponseVo send(@RequestBody @Validated SendMessageReq req, Integer appId)  {
        req.setAppId(appId);
        return ResponseVo.successResponse(pToPMessageService.send(req));
    }

    @RequestMapping("/checkSend")
    public ResponseVo checkSend(@RequestBody @Validated CheckSendMessageReq req)  {
        return pToPMessageService.imServerPermissionCheck(req.getFromId(),req.getToId()
                ,req.getAppId());
    }

    @RequestMapping("/syncOfflineMessage")
    public ResponseVo syncOfflineMessage(@RequestBody
                                         @Validated SyncReq req, Integer appId) throws JsonProcessingException {
        req.setAppId(appId);
        return messageSyncService.syncOfflineMessage(req);
    }
}
