package com.mogen.im.service.message.service;

import com.mogen.im.codec.pack.message.ChatMessageAck;
import com.mogen.im.codec.pack.message.MessageRecycleServiceAckPack;
import com.mogen.im.common.ResponseVo;
import com.mogen.im.common.constants.Constants;
import com.mogen.im.common.enums.ConversationType;
import com.mogen.im.common.enums.MessageAction;
import com.mogen.im.common.messasge.MessageContent;
import com.mogen.im.common.model.ClientInfo;
import com.mogen.im.common.model.message.OfflineMessageContent;
import com.mogen.im.common.utils.BeanUtils;
import com.mogen.im.service.message.model.req.SendMessageReq;
import com.mogen.im.service.message.model.resp.SendMessageResp;
import com.mogen.im.service.seq.RedisSeq;
import com.mogen.im.service.utils.ConversationIdGenerate;
import com.mogen.im.service.utils.MessageProducer;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
public class PToPMessageService {

    @Autowired
    private CheckSendMessageService checkSendMessageService;

    @Autowired
    private MessageProducer messageProducer;

    @Autowired
    private MessageStoreService messageStoreService;

    @Autowired
    private RedisSeq redisSeq;

    private final ThreadPoolExecutor threadPoolExecutor;

    {

        AtomicInteger num = new AtomicInteger(0);

        threadPoolExecutor = new ThreadPoolExecutor(8, 8, 60, TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(1000), new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r);
                thread.setName("message-process-" + num.getAndIncrement());
                thread.setDaemon(true);
                return thread;
            }
        });
    }

    public void process(MessageContent messageContent){

        MessageContent messageCache = messageStoreService.getMessageCache(messageContent.getAppId(),messageContent.getMessageId(),MessageContent.class);
        if(messageCache != null){
            threadPoolExecutor.execute(() -> {
                ack(messageContent,ResponseVo.successResponse());
                syncToSender(messageCache);
                List<ClientInfo> clientInfos =  dispatchMessage(messageCache);
                if(clientInfos.isEmpty()){
                    reciverAck(messageCache);
                }

            });
            return;
        }

        long seq = redisSeq.doGetSeq(messageContent.getAppId() + ":" + Constants.SeqConstants.Message + ":"+
                ConversationIdGenerate.generateP2PId(messageContent.getFromId(),messageContent.getToId()));
        messageContent.setMessageSequence(seq);

        threadPoolExecutor.execute(() -> {
                messageStoreService.storePToPMessage(messageContent);

                OfflineMessageContent offlineMessageContent = new OfflineMessageContent();
                BeanUtils.copyPropertiesIgnoreNull(messageContent,offlineMessageContent);
                offlineMessageContent.setConversationType(ConversationType.P_TO_P.getValue());
                messageStoreService.storeOfflineMessage(offlineMessageContent);

                ack(messageContent,ResponseVo.successResponse());
                syncToSender(messageContent);
                List<ClientInfo> clientInfos =  dispatchMessage(messageContent);
                messageStoreService.storeMessageCacheId(messageContent.getAppId(),messageContent.getMessageId(),messageContent);
                if(clientInfos.isEmpty()){
                    reciverAck(messageContent);
                }

            });
    }

    public SendMessageResp send(SendMessageReq req) {

        SendMessageResp sendMessageResp = new SendMessageResp();
        MessageContent message = new MessageContent();
        BeanUtils.copyPropertiesIgnoreNull(req,message);
        messageStoreService.storePToPMessage(message);
        sendMessageResp.setMessageKey(message.getMessageKey());
        sendMessageResp.setMessageTime(System.currentTimeMillis());

        syncToSender(message);

        dispatchMessage(message);
        return sendMessageResp;
    }

    public void reciverAck(MessageContent messageContent){
        MessageRecycleServiceAckPack pack = new MessageRecycleServiceAckPack();
        pack.setFromId(messageContent.getToId());
        pack.setToId(messageContent.getFromId());
        pack.setMessageKey(messageContent.getMessageKey());
        pack.setServerSend(true);
        messageProducer.sendToUser(messageContent.getFromId(),MessageAction.MSG_RECIVE_ACK,
                pack,new ClientInfo(messageContent.getAppId(),messageContent.getClientType()
                        ,messageContent.getImei()));
    }


    private List<ClientInfo> dispatchMessage(MessageContent messageContent){
        List<ClientInfo> clientInfos =
                messageProducer.sendToUser(messageContent.getToId(),MessageAction.MSG_P2P,messageContent,messageContent.getAppId());
        return clientInfos;
    }

    private void ack(MessageContent messageContent,ResponseVo responseVo) {
        log.info("SEND ACK:[ messageId: {}  responseCode: {} ]", messageContent.getMessageId(), responseVo.getCode());
        ChatMessageAck chatMessageAck = new ChatMessageAck(messageContent.getMessageId(),messageContent.getMessageSequence());
        responseVo.setData(chatMessageAck);
        messageProducer.sendToUser(messageContent.getFromId(), MessageAction.MSG_ACK,chatMessageAck,messageContent);
    }

    private void syncToSender(MessageContent messageContent){
        messageProducer.sendToUserExceptClient(messageContent.getFromId(),MessageAction.MSG_P2P,
                messageContent,messageContent);
    }

    public ResponseVo imServerPermissionCheck(String fromId,String toId,Integer appId){

        ResponseVo responseVo = checkSendMessageService.checkSenderForvidAndMute(fromId,appId);
        if(!responseVo.isOk()){
            return responseVo;
        }

        ResponseVo friendShipCheckResp = checkSendMessageService.checkFriendShip(fromId,toId,appId);
        return friendShipCheckResp;
    }
}
