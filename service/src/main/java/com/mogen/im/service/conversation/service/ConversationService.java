package com.mogen.im.service.conversation.service;


import com.mogen.im.common.ResponseVo;
import com.mogen.im.common.constants.Constants;
import com.mogen.im.common.enums.ConversationType;
import com.mogen.im.common.model.SyncReq;
import com.mogen.im.common.model.SyncResp;
import com.mogen.im.common.model.message.MessageReadedContent;
import com.mogen.im.service.conversation.entity.Conversation;
import com.mogen.im.service.conversation.entity.ConversationId;
import com.mogen.im.service.conversation.repository.ConversationRepository;
import com.mogen.im.service.seq.RedisSeq;
import com.mogen.im.service.utils.MessageProducer;
import com.mogen.im.service.utils.WriteUserSeq;
import com.mogen.im.common.utils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ConversationService {


    @Autowired
    private ConversationRepository conversationRepository;

    @Autowired
    private RedisSeq redisSeq;

    @Autowired
    private WriteUserSeq writeUserSeq;


    public ConversationId convertConversationId(Integer type, String fromId, String toId, Integer appId){
        ConversationId conversationId = new ConversationId();;
        conversationId.setAppId(appId);
        conversationId.setToId(toId);
        conversationId.setFromId(fromId);
        return conversationId;
    }

    @Transactional
    public void messageMarkRead(MessageReadedContent messageContent) {
        String toId = messageContent.getToId();
        if(messageContent.getConversationType().equals(ConversationType.GROUP.ordinal())){
             toId = messageContent.getGroupId().toString();
        }

        ConversationId conversationId = convertConversationId(messageContent.getConversationType(),
                messageContent.getFromId(),toId,messageContent.getAppId());

        Optional<Conversation> conversationOptional  = conversationRepository.findById(conversationId);
        long seq = redisSeq.doGetSeq(messageContent.getAppId() + ":" + Constants.SeqConstants.Conversation);
        if(!conversationOptional.isPresent()){
            Conversation conversation = new Conversation();
            BeanUtils.copyPropertiesIgnoreNull(messageContent,conversation);
            conversation.setType(messageContent.getConversationType());
            conversation.setReadedSequence(messageContent.getMessageSequence());
            conversation.setSequence(seq);
            conversationRepository.save(conversation);
        }else {
            seq = redisSeq.doGetSeq(messageContent.getAppId() + ":" + Constants.SeqConstants.Conversation);
            conversationRepository.readMark(conversationId,messageContent.getMessageSequence(),seq);
        }
        writeUserSeq.writeUserSeq(messageContent.getAppId(),messageContent.getFromId(),
                Constants.SeqConstants.FriendshipGroup,seq);

    }


    public ResponseVo syncConversationSet(SyncReq syncReq){
        SyncResp<Conversation> syncResp = new SyncResp<>();
        if(syncReq.getMaxLimit() > 100){
            syncReq.setMaxLimit(100);
        }
        List<Conversation> conversations = conversationRepository.findByMaxSeqAndLimit(syncReq.getOperator(),syncReq.getAppId(),syncReq.getLastSequence(),syncReq.getMaxLimit());
        if(conversations != null && !conversations.isEmpty()){
            Conversation lastConversation = conversations.get(conversations.size() -1);
            Long maxSeq = conversationRepository.findMaxSeqByFromIdAndAppId(syncReq.getOperator(),syncReq.getAppId());
            BeanUtils.copyPropertiesIgnoreNull(syncReq,syncResp);
            syncResp.setDataList(conversations);
            syncResp.setMaxSequence(maxSeq);
            syncResp.setCompleted(lastConversation.getSequence() >= maxSeq);
            return ResponseVo.successResponse(syncResp);
        }
        return ResponseVo.successResponse();
    }



}
