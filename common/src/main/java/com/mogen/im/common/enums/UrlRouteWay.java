package com.mogen.im.common.enums;

import com.mogen.im.common.route.algorithm.consistenthash.ConsistentHashHandle;
import com.mogen.im.common.route.algorithm.loop.LoopHandle;
import com.mogen.im.common.route.algorithm.random.RandomHandle;

public enum UrlRouteWay{

    RAMDOM(RandomHandle.class),



    LOOP(LoopHandle.class),

    HASH(ConsistentHashHandle.class),
    ;

    private Class clazz;



    UrlRouteWay(Class clazz){
        this.clazz = clazz;
    }

    public Class getClazz() {
        return clazz;
    }

}
