package com.mogen.im.tcp.utils;

import com.mogen.im.codec.config.BootstrapConfig;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeoutException;

public class MqFactory {

    private static ConnectionFactory factory = null;

    private static Channel defaultChannel;

    private static ConcurrentHashMap<String,Channel> channelMap = new ConcurrentHashMap<>();

    public static Channel getChannel(String channelName) throws IOException, TimeoutException {
        if(channelMap.containsKey(channelName)){
            return channelMap.get(channelName);
        }
        Connection connection = getConnection();
        Channel channel = connection.createChannel();
        channelMap.put(channelName,channel);
        return channel;
    }


    public static Connection getConnection() throws IOException, TimeoutException {
        Connection connection = null;
        connection = factory.newConnection();
        return connection;
    }

    public static void init(BootstrapConfig.Rabbitmq rabbitmq) {
        if(factory == null){
            factory = new ConnectionFactory();
            factory.setHost(rabbitmq.getHost());
            factory.setPort(rabbitmq.getPort());
            factory.setUsername(rabbitmq.getUserName());
            factory.setPassword(rabbitmq.getPassword());
            factory.setVirtualHost(rabbitmq.getVirtualHost());
        }
    }
}
