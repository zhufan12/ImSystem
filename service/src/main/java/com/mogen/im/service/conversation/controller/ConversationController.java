package com.mogen.im.service.conversation.controller;

import com.mogen.im.common.ResponseVo;
import com.mogen.im.common.model.SyncReq;
import com.mogen.im.service.conversation.service.ConversationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("v1/conversation")
public class ConversationController {


    @Autowired
    private ConversationService conversationService;


    @RequestMapping("/syncConversationList")
    public ResponseVo syncFriendShipList(@RequestBody @Validated SyncReq req, Integer appId)  {
        req.setAppId(appId);
        return conversationService.syncConversationSet(req);
    }


}
