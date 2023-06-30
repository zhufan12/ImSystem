package com.mogen.im.common.model;

import com.mogen.im.common.enums.ConnectStatus;
import lombok.Data;

@Data
public class UserSession {
    private String userId;

    private Integer appId;

    private Integer clientType;

    private Integer version;

    private Integer connectState;

    private Integer brokerId;

    private String brokerHost;

    private String imei;
}
