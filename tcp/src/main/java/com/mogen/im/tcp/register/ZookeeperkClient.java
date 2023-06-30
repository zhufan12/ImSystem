package com.mogen.im.tcp.register;

import com.mogen.im.codec.config.BootstrapConfig;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;
import org.apache.zookeeper.CreateMode;


public class ZookeeperkClient {

    private static BootstrapConfig.ZkConfig zkConfig;

    private static CuratorFramework zkClient;

    public ZookeeperkClient(BootstrapConfig.ZkConfig zkConfig) {
        this.zkConfig = zkConfig;
    }

    public void init(){
        int sleepMsBetweenRetries = 100;
        int maxRetries = 3;
        RetryPolicy retryPolicy = new RetryNTimes(
                maxRetries, sleepMsBetweenRetries);

        CuratorFramework client = CuratorFrameworkFactory
                .newClient(zkConfig.getZkAddr(),zkConfig.getSessionTimeoutMs(),zkConfig.getZkConnectTimeOut(), retryPolicy);
        client.start();
        zkClient = client;
    }

    public CuratorFramework getZooKeeperInstance(){
        return zkClient;
    }


    public boolean exists(String path){
        try {
            return zkClient.checkExists().forPath(path) != null;
        } catch (Exception e) {
            return false;
        }
    }

    public void createPersistentNode(String path){
        try {
            zkClient.create().creatingParentContainersIfNeeded()
                    .withMode(CreateMode.PERSISTENT)
                    .forPath(path,"init".getBytes());
        } catch (Exception e) {
           e.printStackTrace();
        }
    }


}
