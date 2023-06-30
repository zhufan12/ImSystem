package com.mogen.im.common.route.algorithm.consistenthash;

import com.mogen.im.common.route.RouteHandle;

import java.util.List;

public class ConsistentHashHandle implements RouteHashHandle {

    private AbstractConsistentHash hash;

    public void setHash(AbstractConsistentHash hash) {
        this.hash = hash;
    }


    @Override
    public String routeServer(List<String> nodes, String key) {
        return hash.process(nodes,key);
    }
}
