package com.mogen.im.common.route;

import lombok.Data;

@Data
public class RouteInfo {

    private String ip;
    private Integer port;

    public RouteInfo(String ip, Integer port) {
        this.ip = ip;
        this.port = port;
    }
}
