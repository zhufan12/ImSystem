package com.mogen.im.tcp;


import com.mogen.im.codec.config.BootstrapConfig;
import com.mogen.im.tcp.receiver.MessageReceiver;
import com.mogen.im.tcp.redis.RedisManager;
import com.mogen.im.tcp.register.RegistryZooKeeper;
import com.mogen.im.tcp.register.ZKit;
import com.mogen.im.tcp.register.ZookeeperkClient;
import com.mogen.im.tcp.server.ImServer;
import com.mogen.im.tcp.server.WebSocketServer;
import com.mogen.im.tcp.utils.MqFactory;
import org.redisson.api.RedissonClient;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class Starter {


    public static void main(String[] args) {
        if(args.length >= 1){
            start(args[0]);
        }
    }

    private static void start(String path){
        try {
            Yaml yaml = new Yaml();
            InputStream inputStream = new FileInputStream(path);;
            BootstrapConfig bootstrapConfig = yaml.loadAs(inputStream, BootstrapConfig.class);
            new WebSocketServer(bootstrapConfig.getIm()).start();
            new ImServer(bootstrapConfig.getIm()).start();
            RedisManager.init(bootstrapConfig);

            MqFactory.init(bootstrapConfig.getIm().getRabbitmq());

            MessageReceiver.init(bootstrapConfig.getIm().getBrokerId().toString());

            registerZK(bootstrapConfig);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    public static void registerZK(BootstrapConfig config) throws UnknownHostException {
        String hostAddress = InetAddress.getLocalHost().getHostAddress();
        ZookeeperkClient zookeeperkClient = new ZookeeperkClient(config.getIm().getZkConfig());
        zookeeperkClient.init();
        ZKit zKit = new ZKit(zookeeperkClient);
        RegistryZooKeeper registryZK = new RegistryZooKeeper(zKit, hostAddress, config.getIm());
        Thread thread = new Thread(registryZK);
        thread.start();
    }
}
