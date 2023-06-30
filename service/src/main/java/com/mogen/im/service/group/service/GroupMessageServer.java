package com.mogen.im.service.group.service;

import com.mogen.im.codec.pack.message.ChatMessageAck;
import com.mogen.im.common.ResponseVo;
import com.mogen.im.common.constants.Constants;
import com.mogen.im.common.enums.GroupEventAction;
import com.mogen.im.common.enums.MessageAction;
import com.mogen.im.common.messasge.GroupMessageContent;
import com.mogen.im.common.messasge.MessageContent;
import com.mogen.im.common.model.message.OfflineMessageContent;
import com.mogen.im.common.utils.BeanUtils;
import com.mogen.im.service.group.modle.req.SendGroupMessageReq;
import com.mogen.im.service.message.model.resp.SendMessageResp;
import com.mogen.im.service.message.service.CheckSendMessageService;
import com.mogen.im.service.message.service.MessageStoreService;
import com.mogen.im.service.message.service.PToPMessageService;
import com.mogen.im.service.seq.RedisSeq;
import com.mogen.im.service.utils.MessageProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class GroupMessageServer {

    private static final Logger logger = LoggerFactory.getLogger(GroupMessageServer.class);

    @Autowired
    private CheckSendMessageService checkSendMessageService;

    @Autowired
    private GroupMemberService groupMemberService;

    @Autowired
    private MessageProducer messageProducer;


    @Autowired
    private MessageStoreService messageStoreService;

    @Autowired
    private RedisSeq redisSeq;


    private ThreadPoolExecutor threadPoolExecutor;


    {
        AtomicLong num = new AtomicLong(1);
        threadPoolExecutor = new ThreadPoolExecutor(8, 8, 60, TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(1000), new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r);
                thread.setName("message-group-process-" + num.getAndIncrement());
                thread.setDaemon(true);
                return thread;
            }
        });
    }



    public void process(GroupMessageContent messageContent){
        String fromId = messageContent.getFromId();
        Integer groupId = messageContent.getGroupId();
        Integer appId = messageContent.getAppId();

        GroupMessageContent cacheGroupMessage = messageStoreService.getMessageCache(messageContent.getAppId(),
                messageContent.getMessageId(),GroupMessageContent.class);
        if(cacheGroupMessage != null){
            threadPoolExecutor.execute(()-> {
                List<String> groupMemberIds = groupMemberService.getGroupMemberId(messageContent.getGroupId());
                ack(messageContent,ResponseVo.successResponse());
                syncToSender(messageContent);
                dispatchMessage(messageContent,groupMemberIds);
            });
        }

        ResponseVo checkResp = imServerPermissionCheck(fromId,groupId,appId);
        if(checkResp.isOk()){
            Long seq = redisSeq.doGetSeq(messageContent.getAppId() + ":" +
                    Constants.SeqConstants.Group + ":" + messageContent.getGroupId());
            messageContent.setMessageSequence(seq);

            threadPoolExecutor.execute(()-> {
                messageStoreService.storeGroupMessage(messageContent);
                OfflineMessageContent offlineMessageContent = new OfflineMessageContent();
                BeanUtils.copyPropertiesIgnoreNull(messageContent,offlineMessageContent);
                List<String> groupMemberIds = groupMemberService.getGroupMemberId(messageContent.getGroupId());
                messageStoreService.storeGroupOfflineMessage(offlineMessageContent,groupMemberIds);

                ack(messageContent,checkResp);
                syncToSender(messageContent);
                dispatchMessage(messageContent,groupMemberIds);
                messageStoreService.storeMessageCacheId(messageContent.getAppId(),messageContent.getMessageId(),messageContent);
            });

            return;
        }

        ack(messageContent,checkResp);

    }


    private void dispatchMessage(GroupMessageContent messageContent, List<String> memberIds){
        for(String member : memberIds){
            if(!member.equals(messageContent.getFromId())){
                messageProducer.sendToUser(messageContent.getToId(), GroupEventAction.MSG_GROUP,messageContent,messageContent.getAppId());
            }
        }
    }

    private void ack(GroupMessageContent messageContent,ResponseVo responseVo) {
        logger.info("SEND ACK:[ messageId: {}  responseCode: {} ]", messageContent.getMessageId(), responseVo.getCode());
        ChatMessageAck chatMessageAck = new ChatMessageAck(messageContent.getMessageId(),messageContent.getMessageSequence());
        responseVo.setData(chatMessageAck);
        messageProducer.sendToUser(messageContent.getFromId(), GroupEventAction.GROUP_MSG_ACK,chatMessageAck,messageContent);
    }

    private void syncToSender(GroupMessageContent messageContent){
        messageProducer.sendToUserExceptClient(messageContent.getFromId(),GroupEventAction.MSG_GROUP,
                messageContent,messageContent);
    }

    private ResponseVo imServerPermissionCheck(String fromId,Integer groupId,Integer appId){

        ResponseVo responseVo = checkSendMessageService.checkSenderForvidAndMute(fromId,appId);
        if(!responseVo.isOk()){
            return responseVo;
        }

        ResponseVo checkGroupResp = checkSendMessageService.checkGroupMessage(fromId,groupId,appId);
        return checkGroupResp;
    }

    public SendMessageResp send(SendGroupMessageReq req) {

        SendMessageResp sendMessageResp = new SendMessageResp();
        GroupMessageContent message = new GroupMessageContent();
        BeanUtils.copyPropertiesIgnoreNull(message,req);
        List<String> groupMemberIds =groupMemberService.getGroupMemberId(req.getGroupId());
        messageStoreService.storeGroupMessage(message);

        sendMessageResp.setMessageKey(message.getMessageKey());
        sendMessageResp.setMessageTime(System.currentTimeMillis());
        syncToSender(message);
        dispatchMessage(message,groupMemberIds);

        return sendMessageResp;

    }
}
