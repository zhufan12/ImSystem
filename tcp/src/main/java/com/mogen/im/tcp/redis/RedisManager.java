package com.mogen.im.tcp.redis;

import com.mogen.im.codec.config.BootstrapConfig;
import com.mogen.im.tcp.receiver.UserLoginMessageListener;
import org.redisson.api.RedissonClient;

public class RedisManager {

    private static RedissonClient redissonClient;


    public static void init(BootstrapConfig config){
        SingleClientStrategy singleClientStrategy = new SingleClientStrategy();
        redissonClient = singleClientStrategy.getRedissonClient(config.getIm().getRedis());

        UserLoginMessageListener userLoginMessageListener = new UserLoginMessageListener(config.getIm().getLoginModel());
        userLoginMessageListener.listenerUserLogin();
    }

    public static RedissonClient getRedissonClient(){
        return redissonClient;
    }

}
