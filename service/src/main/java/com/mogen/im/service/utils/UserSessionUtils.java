package com.mogen.im.service.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mogen.im.common.constants.Constants;
import com.mogen.im.common.enums.ConnectStatus;
import com.mogen.im.common.model.UserSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Component
public class UserSessionUtils {

        public Object get;

        @Autowired
        StringRedisTemplate stringRedisTemplate;

        @Autowired
        private ObjectMapper objectMapper;

        public List<UserSession> getUserSession(Integer appId, String userId) throws JsonProcessingException {

            String userSessionKey = appId + Constants.RedisConstants.UserSessionConstants
                    + userId;
            Map<Object, Object> entries = stringRedisTemplate.opsForHash().entries(userSessionKey);
            List<UserSession> list = new ArrayList<>();
            Collection<Object> values = entries.values();
            for (Object o : values){
                String str = (String) o;
                UserSession session = objectMapper.readValue(str, UserSession.class);
                if(session.getConnectState().equals(ConnectStatus.ONLINE.ordinal())){
                    list.add(session);
                }
            }
            return list;
        }

        public UserSession getUserSession(Integer appId,String userId
                ,Integer clientType,String imei) throws JsonProcessingException {

            String userSessionKey = appId + Constants.RedisConstants.UserSessionConstants
                    + userId;
            String hashKey = clientType + ":" + imei;
            Object o = stringRedisTemplate.opsForHash().get(userSessionKey, hashKey);
            UserSession session = objectMapper.readValue(o.toString(), UserSession.class);
            return session;
        }

}