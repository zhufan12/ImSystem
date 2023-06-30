package com.mogen.im.service.group.mq;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mogen.im.common.constants.Constants;
import com.mogen.im.common.enums.ConversationType;
import com.mogen.im.common.enums.GroupEventAction;
import com.mogen.im.common.messasge.GroupMessageContent;
import com.mogen.im.common.messasge.MessageContent;
import com.mogen.im.common.model.message.MessageReadedContent;
import com.mogen.im.service.group.service.GroupMessageServer;
import com.mogen.im.service.message.service.MessageSyncService;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
public class GroupChatOperateReceiver {


    private static final Logger logger = LoggerFactory.getLogger(GroupChatOperateReceiver.class);

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private GroupMessageServer groupMessageServer;

    @Autowired
    private MessageSyncService messageSyncService;




    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = Constants.RabbitConstants.ImToGroupService,declare = "true"),
            exchange = @Exchange(value = Constants.RabbitConstants.ImToGroupService,declare = "true")),concurrency = "1")
    public void onMessage(@Payload Message message, @Headers Map<String,Object> headers,
                          Channel channel) throws IOException {
        String msg = new String(message.getBody());

        logger.info("CHAT GROUP MSG FROM QUEUE: [{}]",msg);
        Long deliveryTag = (Long) headers.get(AmqpHeaders.DELIVERY_TAG);
        try {
            JsonNode jsonNode = objectMapper.readTree(message.getBody());
            Integer action = jsonNode.get("action").asInt();
            if(action.equals(GroupEventAction.MSG_GROUP.getAction())){
                GroupMessageContent messageContent = objectMapper.readValue(jsonNode.toString(),GroupMessageContent.class);
                groupMessageServer.process(messageContent);
            }else if (action.equals(GroupEventAction.MSG_GROUP_READED.getAction())) {
                MessageReadedContent messageReaded = objectMapper.readValue(jsonNode.toString(),MessageReadedContent.class);
                messageReaded.setConversationType(ConversationType.GROUP.getValue());
                messageSyncService.groupReadMark(messageReaded);
            }
            channel.basicAck(deliveryTag,false);
        }catch (Exception ex){
            logger.error("HANDLE MESSAGE EXCEPTION MSG：{}", ex.getMessage());
            logger.error("RMQ_CHAT_TRAN_ERROR", ex);
            logger.error("NACK_MSG:{}", msg);
            channel.basicNack(deliveryTag, false, false);

        }

    }
}
