package com.mogen.im.common.model;

import lombok.Data;
@Data
public class RequestBase {

    private Integer appId;

    private String operator;

    private Integer clientType;

    private String imei;
}
