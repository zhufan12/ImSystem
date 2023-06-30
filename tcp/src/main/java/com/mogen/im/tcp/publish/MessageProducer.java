package com.mogen.im.tcp.publish;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mogen.im.codec.proto.Message;
import com.mogen.im.common.constants.Constants;
import com.mogen.im.common.utils.JsonUtils;
import com.mogen.im.tcp.utils.MqFactory;
import com.rabbitmq.client.Channel;
import jakarta.persistence.OneToMany;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.TimeoutException;

public class MessageProducer {

    private static final Logger log = LoggerFactory.getLogger(MessageProducer.class);

    private static ObjectMapper mapper = JsonUtils.getInstance();

    public static void sendMessage(Message msg,Integer action){
        Channel channel = null;
        String channelName = Constants.RabbitConstants.ImToMessageService;
        if(action.toString().startsWith("2")){
            channelName = Constants.RabbitConstants.ImToGroupService;
        }
        try {
            channel = MqFactory.getChannel(channelName);
            String message = mapper.writeValueAsString(msg.getMessagePack());
            Map<String,Object> map =  mapper.readValue(message,Map.class);
            map.put("action",action);
            map.put("appId",msg.getMessageHeader().getAppId());
            map.put("clientType",msg.getMessageHeader().getClientType());
            map.put("imei",msg.getMessageHeader().getImei());
            map.put("messageTime",System.currentTimeMillis());
            String sendMes = mapper.writeValueAsString(map);
            channel.basicPublish(channelName,"",null, sendMes.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            e.printStackTrace();
            log.error("sendMessage failed,msg: {}", e.getMessage());
        }
    }
}
