package com.mogen.im.service.config;


import com.mogen.im.common.config.AppConfig;
import com.mogen.im.common.enums.UrlRouteWay;
import com.mogen.im.common.route.RouteHandle;
import com.mogen.im.common.route.algorithm.consistenthash.AbstractConsistentHash;
import com.mogen.im.common.route.algorithm.consistenthash.ConsistentHashHandle;
import com.mogen.im.common.route.algorithm.consistenthash.RouteHashHandle;
import com.mogen.im.common.route.algorithm.consistenthash.TreeMapConsistentHash;
import com.mogen.im.common.route.algorithm.loop.LoopHandle;
import com.mogen.im.common.route.algorithm.random.RandomHandle;
import com.mogen.im.service.utils.SnowflakeIdWorker;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Bean;

@Configurable
public class BeanConfig {


    @Autowired
    private AppConfig appConfig;

    @Bean
    public RouteHandle routeHandle() throws Exception {
        UrlRouteWay urlRouteWay = appConfig.getImRouteWay();
        if(urlRouteWay.equals(UrlRouteWay.HASH)) {
            AbstractConsistentHash abstractConsistentHash = (AbstractConsistentHash) appConfig.getConsistentHashWay().getClazz().newInstance();
            RouteHashHandle routeHandle = (RouteHashHandle) urlRouteWay.getClazz().newInstance();
            routeHandle.setHash(abstractConsistentHash);
            return routeHandle;
        }
        return  (RouteHandle) urlRouteWay.getClazz().newInstance();
    }

    @Bean
    public CuratorFramework zkClient(){
        RetryPolicy retryPolicy = new RetryNTimes(
                appConfig.getMaxRetries(), appConfig.getSleepMsBetweenRetries());

        CuratorFramework client = CuratorFrameworkFactory
                .newClient(appConfig.getZkAddr(),appConfig.getSessionTimeoutMs(),appConfig.getZkConnectTimeOut(), retryPolicy);
        client.start();
        return client;
    }

    @Bean
    public SnowflakeIdWorker buildSnowflakeSeq(){
        return new SnowflakeIdWorker(0);
    }
}
