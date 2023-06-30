package com.mogen.im.service.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mogen.im.codec.proto.MessagePack;
import com.mogen.im.common.constants.Constants;
import com.mogen.im.common.enums.action.Action;
import com.mogen.im.common.model.ClientInfo;
import com.mogen.im.common.model.UserSession;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class MessageProducer {


    private static Logger logger = LoggerFactory.getLogger(MessageProducer.class);

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private  UserSessionUtils userSessionUtils;


    @Autowired
    private ObjectMapper objectMapper;


    private String queueName = Constants.RabbitConstants.MessageServiceToIm;


    public boolean sendMessage(UserSession session, Object msg){
        try {
            logger.info("send message == " + msg);
            logger.info("user session ==" + session);
            rabbitTemplate.convertAndSend(queueName + session.getBrokerId(),msg);
            return true;
        }catch (Exception e){
            logger.error("send error :" + e.getMessage());
            return false;
        }
    }

    public boolean sendPack(String toId, Action action,Object message,UserSession userSession) {
        MessagePack messagePack = new MessagePack();
        messagePack.setAction(action.getAction());
        messagePack.setToId(toId);
        messagePack.setClientType(userSession.getClientType());
        messagePack.setImei(userSession.getImei());
        messagePack.setUserId(userSession.getUserId());
        messagePack.setAppId(userSession.getAppId());
        messagePack.setData(message);
        try {
            String data = objectMapper.writeValueAsString(messagePack);
            return sendMessage(userSession, data);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("sendPack failed : [{}]", e.getMessage());
            return false;
        }
    }

    public void sendToUser(String toId, Integer clientType,String imei,Integer appId, Action action,
                           Object data) {
        try {
            if(clientType != null && StringUtils.isNotBlank(imei)){
                ClientInfo clientInfo = new ClientInfo(appId, clientType, imei);
                sendToUserExceptClient(toId,action,data,clientInfo);
            }else{
                sendToUser(toId,action,data,appId);
            }
        }catch (Exception e){
            e.printStackTrace();
            logger.error("sendToUser failed : [{}]",e.getMessage());
        }

    }

    public List<ClientInfo> sendToUser(String toId,Action action,Object data,Integer appId){
        List<ClientInfo> clientInfos = new ArrayList<>();
        try {
            List<UserSession> userSessions = userSessionUtils.getUserSession(appId,toId);
            for (UserSession userSession : userSessions){
               boolean send =  sendPack(toId,action,data,userSession);
               if(send){
                   clientInfos.add(new ClientInfo(userSession.getAppId(),userSession.getClientType(),userSession.getImei()));
               }
            }
        }catch (Exception e){
            e.printStackTrace();
            logger.error("sendToUser failed : [{}]",e.getMessage());
        }
    return clientInfos;
    }

    public void sendToUser(String toId, Action action, Object data, ClientInfo clientInfo) {
        try {
            UserSession userSession = userSessionUtils.getUserSession(clientInfo.getAppId(), toId, clientInfo.getClientType(), clientInfo.getImei());
            sendPack(toId, action, data, userSession);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("sendToUser failed : [{}]", e.getMessage());
        }
    }


    public void sendToUserExceptClient(String toId, Action action
            , Object data, ClientInfo clientInfo) {
        List<UserSession> userSession = null;
        try {
            userSession = userSessionUtils.getUserSession(clientInfo.getAppId(),
                            toId);
            for (UserSession session : userSession) {
                if(!isMatch(session,clientInfo)){
                    sendPack(toId,action,data,session);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("sendToUserExceptClient failed : [{}]", e.getMessage());
        }

    }

    private boolean isMatch(UserSession sessionDto, ClientInfo clientInfo) {
        return Objects.equals(sessionDto.getAppId(), clientInfo.getAppId())
                && Objects.equals(sessionDto.getImei(), clientInfo.getImei())
                && Objects.equals(sessionDto.getClientType(), clientInfo.getClientType());
    }


}
