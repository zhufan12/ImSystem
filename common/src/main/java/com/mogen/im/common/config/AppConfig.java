package com.mogen.im.common.config;

import com.mogen.im.common.enums.RouteHashMethod;
import com.mogen.im.common.enums.UrlRouteWay;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
@Data
@Component
@ConfigurationProperties(prefix = "appconfig")
public class AppConfig {

    private String privateKey;

    private String zkAddr;
    private Integer zkConnectTimeOut;

    private Integer sleepMsBetweenRetries;

    private UrlRouteWay imRouteWay;

    private Integer maxRetries;

    private Integer sessionTimeoutMs;

    private boolean sendMessageCheckFriend;

    private boolean sendMessageCheckBlack;

    private RouteHashMethod consistentHashWay;

    private String callbackUrl;

    private boolean modifyUserAfterCallback;



    private Integer deleteConversationSyncMode;

    private Integer offlineMessageCount;

}
