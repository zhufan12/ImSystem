package com.mogen.im.service.message.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mogen.im.common.constants.Constants;
import com.mogen.im.common.enums.ConversationType;
import com.mogen.im.common.messasge.GroupMessageContent;
import com.mogen.im.common.messasge.MessageContent;
import com.mogen.im.common.model.message.OfflineMessageContent;
import com.mogen.im.common.utils.BeanUtils;
import com.mogen.im.service.conversation.entity.ConversationId;
import com.mogen.im.service.conversation.service.ConversationService;
import com.mogen.im.service.group.entity.GroupMessageHistory;
import com.mogen.im.service.message.entity.MessageBody;
import com.mogen.im.service.message.entity.MessageHistory;
import com.mogen.im.service.group.repostiory.GroupMessageHistoryRepository;
import com.mogen.im.service.message.repository.MessageBodyRepository;
import com.mogen.im.service.message.repository.MessageHistoryRepository;
import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class MessageStoreService {

    private static final Integer offlineMaxQueueNumber = 1000;

    @Autowired
    private MessageBodyRepository messageBodyRepository;

    @Autowired
    private MessageHistoryRepository messageHistoryRepository;

    @Autowired
    private GroupMessageHistoryRepository groupMessageHistoryRepository;


    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private ConversationService conversationService;

    @Autowired
    private ObjectMapper objectMapper;


    @Transactional
    public void storePToPMessage(MessageContent messageContent){
        MessageBody messageBody = extractMessageBody(messageContent);
        MessageBody insert = messageBodyRepository.save(messageBody);

        List<MessageHistory> histories = extractMessageHistory(messageContent,messageBody);
        messageHistoryRepository.saveAll(histories);

        messageContent.setMessageKey(insert.getMessageKey());
    }


    @Transactional
    public void storeGroupMessage(GroupMessageContent messageContent){
        MessageBody messageBody = extractMessageBody(messageContent);
        MessageBody insert = messageBodyRepository.save(messageBody);
        GroupMessageHistory groupMessageHistory = extractGroupMessageHistory(messageContent,insert);
        groupMessageHistoryRepository.save(groupMessageHistory);
        messageContent.setMessageKey(messageBody.getMessageKey());
    }


    protected GroupMessageHistory extractGroupMessageHistory(GroupMessageContent groupMessageContent, MessageBody messageBody){
        GroupMessageHistory groupMessageHistory = new GroupMessageHistory();
        BeanUtils.copyPropertiesIgnoreNull(groupMessageContent,groupMessageHistory);
        groupMessageHistory.setMessageKey(messageBody.getMessageKey());
        groupMessageHistory.setSequence(groupMessageContent.getMessageSequence());
        return groupMessageHistory;
    }

    protected MessageBody extractMessageBody(MessageContent messageContent){
         MessageBody messageBody = new MessageBody();
         messageBody.setAppId(messageContent.getAppId());
         messageBody.setSecurityKey("");
         messageBody.setMessageBody(messageContent.getMessageBody());
         messageBody.setMessageTime(messageContent.getMessageTime());
         messageBody.setExtra(messageContent.getExtra());
         return messageBody;
    }

    protected List<MessageHistory> extractMessageHistory(MessageContent messageContent,MessageBody messageBody){
        List<MessageHistory> messageHistories = new ArrayList<>();
        MessageHistory fromHistory = new MessageHistory();
        MessageHistory toHistory= new MessageHistory();

        BeanUtils.copyPropertiesIgnoreNull(messageContent,fromHistory);
        fromHistory.setOwnerId(messageContent.getFromId());
        fromHistory.setMessageKey(messageBody.getMessageKey());
        fromHistory.setSequence(messageContent.getMessageSequence());

        BeanUtils.copyPropertiesIgnoreNull(messageContent,toHistory);
        toHistory.setOwnerId(messageContent.getToId());
        toHistory.setMessageKey(messageBody.getMessageKey());
        toHistory.setSequence(messageContent.getMessageSequence());

        messageHistories.add(fromHistory);
        messageHistories.add(toHistory);
        return  messageHistories;
    }


    public void storeMessageCacheId(Integer appId,String messageId,Object messageContent){
        String key = appId + ":" + Constants.RedisConstants.cacheMessage
                + ":" + messageId;
        try {
            String payloadJson = objectMapper.writeValueAsString(messageContent);
            stringRedisTemplate.opsForValue().set(key,payloadJson,300, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("STORE CACHE MESSAGE FAILED msg: {}",e.getMessage());
        }
    }

    public <T> T getMessageCache(Integer appId,String messageId,Class<T> clazz){
        try {
            String key = appId + ":" + Constants.RedisConstants.cacheMessage
                    + ":" + messageId;
            String payloadJson = stringRedisTemplate.opsForValue().get(key);
            if(!StringUtils.isBlank(payloadJson)){
                T messageContent1 = objectMapper.readValue(payloadJson,clazz);
                return messageContent1;
            }
        } catch (Exception e) {
            log.error("GET CACHE MESSAGE FAILED msg: {}",e.getMessage());
            return null;
        }
        return null;
    }

    public void storeOfflineMessage(OfflineMessageContent offlineMsg){
        try {
            String formKey = offlineMsg.getAppId() + ":" + Constants.RedisConstants.OfflineMessage + ":" + offlineMsg.getFromId();
            String toKey =  offlineMsg.getAppId() + ":" + Constants.RedisConstants.OfflineMessage + ":" + offlineMsg.getToId();
            ConversationId conversationId;
            String conversationIdJson;
            ZSetOperations<String,String> zSetOperations = stringRedisTemplate.opsForZSet();
                if(zSetOperations.zCard(formKey) > offlineMaxQueueNumber){
                    zSetOperations.remove(formKey,0,0);
                }
                conversationId = conversationService.convertConversationId(
                        ConversationType.P_TO_P.getValue(),offlineMsg.getToId(),formKey,offlineMsg.getAppId());
                 conversationIdJson = objectMapper.writeValueAsString(conversationId);
                offlineMsg.setConversationId(conversationIdJson);
                zSetOperations.add(formKey,objectMapper.writeValueAsString(offlineMsg),offlineMsg.getMessageKey());

            if(zSetOperations.zCard(toKey) > offlineMaxQueueNumber){
                zSetOperations.remove(toKey,0,0);
            }
             conversationId = conversationService.convertConversationId(
                    ConversationType.P_TO_P.getValue(),offlineMsg.getFromId(),toKey,offlineMsg.getAppId());
             conversationIdJson = objectMapper.writeValueAsString(conversationId);
            offlineMsg.setConversationId(conversationIdJson);
            zSetOperations.add(formKey,objectMapper.writeValueAsString(offlineMsg),offlineMsg.getMessageKey());
        } catch (Exception e) {
            log.error("STORE OFFLINE MESSAGE FAILED msg: {}",e.getMessage());
        }
    }


    public void storeGroupOfflineMessage(OfflineMessageContent offlineMsg,List<String> memberIds){
        try {
            ZSetOperations<String, String> operations = stringRedisTemplate.opsForZSet();
            //判断 队列中的数据是否超过设定值
            offlineMsg.setConversationType(ConversationType.GROUP.getValue());
            for (String member : memberIds){
                String cacheKey = offlineMsg.getAppId() + ":" +
                        Constants.RedisConstants.OfflineMessage + ":" +
                        member;
                if(operations.zCard(cacheKey) > offlineMaxQueueNumber){
                    operations.removeRange(cacheKey,0,0);
                }
               ConversationId  conversationId = conversationService.convertConversationId(
                        ConversationType.GROUP.getValue(),member,offlineMsg.getToId(),offlineMsg.getAppId());
               String conversationIdJson = objectMapper.writeValueAsString(conversationId);
               offlineMsg.setConversationId(conversationIdJson);
                operations.add(cacheKey,objectMapper.writeValueAsString(offlineMsg),offlineMsg.getMessageKey());
            }
        } catch (Exception e) {
            log.error("STORE OFFLINE MESSAGE FAILED msg: {}",e.getMessage());
        }
    }



}
