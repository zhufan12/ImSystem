package com.mogen.im.common.route.algorithm.random;

import com.mogen.im.common.enums.UserErrorCode;
import com.mogen.im.common.exception.ApplicationException;
import com.mogen.im.common.route.RouteHandle;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class RandomHandle implements RouteHandle {
    @Override
    public String routeServer(List<String> nodes, String key) {
        if(nodes.isEmpty()){
            throw new ApplicationException(UserErrorCode.SERVER_NOT_AVAILABLE);
        }
        int i = ThreadLocalRandom.current().nextInt(nodes.size());
        return nodes.get(i);
    }
}
