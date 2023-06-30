package com.mogen.im.tcp.utils;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mogen.im.common.constants.Constants;
import com.mogen.im.common.enums.ConnectStatus;
import com.mogen.im.common.model.UserClientDto;
import com.mogen.im.common.model.UserSession;
import com.mogen.im.common.utils.JsonUtils;
import com.mogen.im.tcp.redis.RedisManager;
import io.netty.channel.Channel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import io.netty.util.internal.StringUtil;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class SessionSocketHolder {

    private static final Map<UserClientDto, NioSocketChannel> CHANNELS = new ConcurrentHashMap<>();

    public static void put(Integer appId,String userId,Integer clientType,String imei,NioSocketChannel channel){
        UserClientDto dto = new UserClientDto();
        dto.setAppId(appId);
        dto.setClientType(clientType);
        dto.setImei(imei);
        dto.setUserId(userId);
        CHANNELS.put(dto,channel);
    }

    public static NioSocketChannel get(Integer appId,String userId,Integer clientType,String imei){
        UserClientDto dto = new UserClientDto();
        dto.setAppId(appId);
        dto.setImei(imei);
        dto.setClientType(clientType);
        dto.setUserId(userId);
        return CHANNELS.get(dto);
    }

    public static List<NioSocketChannel> get(Integer appId , String id) {

        Set<UserClientDto> channelInfos = CHANNELS.keySet();
        List<NioSocketChannel> channels = new ArrayList<>();

        channelInfos.forEach(channel ->{
            if(channel.getAppId().equals(appId) && id.equals(channel.getUserId())){
                channels.add(CHANNELS.get(channel));
            }
        });

        return channels;
    }

    public static void remove(Integer appId,String userId,Integer clientType,String imei){
        UserClientDto dto = new UserClientDto();
        dto.setAppId(appId);
        dto.setClientType(clientType);
        dto.setUserId(userId);
        dto.setImei(imei);
        CHANNELS.remove(dto);
    }


    public static void remove(NioSocketChannel channel){
        CHANNELS.entrySet().stream().filter(entity -> entity.getValue() == channel)
                .forEach(entry -> CHANNELS.remove(entry.getKey()));
    }

    public static void removeUserSession(NioSocketChannel channel){
        String userId = (String) channel.attr(AttributeKey.valueOf(Constants.UserId)).get();
        Integer appId = (Integer) channel.attr(AttributeKey.valueOf(Constants.AppId)).get();
        Integer clientType = (Integer) channel.attr(AttributeKey.valueOf(Constants.ClientType)).get();
        String imei = (String) channel.attr(AttributeKey.valueOf(Constants.Imei)).get();
        RedissonClient reredissonClient = RedisManager.getRedissonClient();

        RMap<String, String> redissonMap = reredissonClient.getMap(appId + Constants.RedisConstants.UserSessionConstants + userId);
        redissonMap.remove(clientType + ":" + imei);
        SessionSocketHolder.remove(appId, userId, clientType,imei);
        channel.close();
    }

    public static void offlineUserSession(NioSocketChannel channel){
        String userId = (String) channel.attr(AttributeKey.valueOf(Constants.UserId)).get();
        Integer appId = (Integer) channel.attr(AttributeKey.valueOf(Constants.AppId)).get();
        Integer clientType = (Integer) channel.attr(AttributeKey.valueOf(Constants.ClientType)).get();
        String imei = (String) channel.attr(AttributeKey.valueOf(Constants.Imei)).get();
        RedissonClient reredissonClient = RedisManager.getRedissonClient();

        SessionSocketHolder.remove(appId, userId, clientType,imei);
        RMap<String, String> redissonMap = reredissonClient.getMap(appId + Constants.RedisConstants.UserSessionConstants + userId);
        String sessionStr = redissonMap.get(clientType + ":" + imei);
        if(!StringUtil.isNullOrEmpty(sessionStr)){
            try {
                UserSession userSession = JsonUtils.getInstance().readValue(sessionStr,UserSession.class);
                userSession.setConnectState(ConnectStatus.OFFLIN.ordinal());
                redissonMap.put(clientType + ":" + imei, JsonUtils.getInstance().writeValueAsString(userSession));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }

    channel.close();
    }

}
