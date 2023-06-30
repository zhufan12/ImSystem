package com.mogen.im.codec.config;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.redisson.api.redisnode.RedisSingle;

@Data
public class BootstrapConfig {

    private TcpConfig im;


    @Data
    public static class TcpConfig{
        private Integer tcpPort;
        private Integer webSocketPort;
        private Integer  boosThreadSize;
        private Integer  workThreadSize;
        private Long heartBeatTime;

        private Integer brokerId;

        private Integer loginModel;

        private String logicUrl;

        private RedisConfig redis;

        private Rabbitmq rabbitmq;

        private ZkConfig zkConfig;
    }


    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RedisConfig {

        private String mode;

        private Integer database;

        private String password;

        private Integer timeout;

        private Integer poolMinIdle;

        private Integer poolConnTimeout;

        private Integer poolSize;

        private String host;

    }


    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Rabbitmq {
        private String host;

        private Integer port;

        private String virtualHost;

        private String userName;

        private String password;
    }


    @Data
    public static class ZkConfig {
        private String zkAddr;

        private Integer zkConnectTimeOut;

        private Integer sessionTimeoutMs;
    }

}
