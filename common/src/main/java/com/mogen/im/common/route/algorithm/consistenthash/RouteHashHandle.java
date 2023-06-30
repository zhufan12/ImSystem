package com.mogen.im.common.route.algorithm.consistenthash;

import com.mogen.im.common.route.RouteHandle;

public interface RouteHashHandle extends RouteHandle {
    public void setHash(AbstractConsistentHash hash);

}
