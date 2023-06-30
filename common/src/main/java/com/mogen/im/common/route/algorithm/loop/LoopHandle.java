package com.mogen.im.common.route.algorithm.loop;

import com.mogen.im.common.enums.UserErrorCode;
import com.mogen.im.common.exception.ApplicationException;
import com.mogen.im.common.route.RouteHandle;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class LoopHandle implements RouteHandle {

    private AtomicLong index = new AtomicLong();
    @Override
    public String routeServer(List<String> nodes, String key) {
        if(nodes.isEmpty()){
            throw new ApplicationException(UserErrorCode.SERVER_NOT_AVAILABLE);
        }
        Long nodeIndex = index.incrementAndGet() % nodes.size();
        if(nodeIndex < 0){
            nodeIndex = 0L;
        }
        return nodes.get(nodeIndex.intValue());
    }
}
