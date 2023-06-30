package com.mogen.im.service.message.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mogen.im.codec.pack.message.MessageReadedPack;
import com.mogen.im.common.ResponseVo;
import com.mogen.im.common.constants.Constants;
import com.mogen.im.common.enums.GroupEventAction;
import com.mogen.im.common.enums.MessageAction;
import com.mogen.im.common.enums.action.Action;
import com.mogen.im.common.model.ClientInfo;
import com.mogen.im.common.model.SyncReq;
import com.mogen.im.common.model.SyncResp;
import com.mogen.im.common.model.message.MessageReadedContent;
import com.mogen.im.common.model.message.MessageRecycleAckContent;
import com.mogen.im.common.model.message.OfflineMessageContent;
import com.mogen.im.service.conversation.service.ConversationService;
import com.mogen.im.service.utils.MessageProducer;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class MessageSyncService {


    @Autowired
    private MessageProducer messageProducer;


    @Autowired
    private ConversationService conversationService;


    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RedisTemplate redisTemplate;




    public void receiveMark(MessageRecycleAckContent msg){
        messageProducer.sendToUser(msg.getToId(), MessageAction.MSG_RECIVE_ACK,
                msg,msg.getAppId());
    }


    public void readMark(MessageReadedContent readedContent){
        conversationService.messageMarkRead(readedContent);

        MessageReadedPack messageReadedPack = new MessageReadedPack();
        BeanUtils.copyProperties(readedContent,messageReadedPack);

        sendToSender(messageReadedPack,readedContent,MessageAction.MSG_READED_NOTIFY);

        messageProducer.sendToUser(readedContent.getToId(), MessageAction.MSG_READED_RECEIPT,
                readedContent,readedContent.getAppId());
    }

    private void sendToSender(MessageReadedPack readedPack, ClientInfo clientInfo, Action action){

        messageProducer.sendToUserExceptClient(readedPack.getFromId(), action,
                readedPack,clientInfo);
    }


    public void groupReadMark(MessageReadedContent readedContent){
        conversationService.messageMarkRead(readedContent);
        MessageReadedPack messageReadedPack = new MessageReadedPack();
        BeanUtils.copyProperties(readedContent,messageReadedPack);
        sendToSender(messageReadedPack,readedContent, GroupEventAction.MSG_GROUP_READED_NOTIFY);
        if(!readedContent.getFromId().equals(readedContent.getToId())){
            messageProducer.sendToUser(messageReadedPack.getToId(),GroupEventAction.MSG_GROUP_READED_RECEIPT
                    ,readedContent,readedContent.getAppId());
        }
    }


    public ResponseVo syncOfflineMessage(SyncReq req) throws JsonProcessingException {
        SyncResp<OfflineMessageContent> resp = new SyncResp<>();
        String key = req.getAppId() + ":" + Constants.RedisConstants.OfflineMessage + ":" + req.getOperator();
        //获取最大的seq
        Long maxSeq = 0L;
        ZSetOperations zSetOperations = redisTemplate.opsForZSet();
        Set set = zSetOperations.reverseRangeWithScores(key, 0, 0);
        if(!CollectionUtils.isEmpty(set)){
            List list = new ArrayList(set);
            DefaultTypedTuple o = (DefaultTypedTuple) list.get(0);
            maxSeq = o.getScore().longValue();
        }

        List<OfflineMessageContent> respList = new ArrayList<>();
        resp.setMaxSequence(maxSeq);

        Set<ZSetOperations.TypedTuple> querySet = zSetOperations.rangeByScoreWithScores(key,
                req.getLastSequence(), maxSeq, 0, req.getMaxLimit());
        for (ZSetOperations.TypedTuple<String> typedTuple : querySet) {
            String value = typedTuple.getValue();

            OfflineMessageContent offlineMessageContent = objectMapper.readValue(value, OfflineMessageContent.class);
            respList.add(offlineMessageContent);
        }
        resp.setDataList(respList);

        if(!CollectionUtils.isEmpty(respList)){
            OfflineMessageContent offlineMessageContent = respList.get(respList.size() - 1);
            resp.setCompleted(maxSeq <= offlineMessageContent.getMessageKey());
        }

        return ResponseVo.successResponse(resp);
    }
}
