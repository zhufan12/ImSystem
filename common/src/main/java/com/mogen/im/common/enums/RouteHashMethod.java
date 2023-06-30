package com.mogen.im.common.enums;

import com.mogen.im.common.route.algorithm.consistenthash.TreeMapConsistentHash;

public enum RouteHashMethod {

    TREE(TreeMapConsistentHash.class);



    private Class clazz;

    RouteHashMethod(Class clazz) {
        this.clazz = clazz;
    }


    public Class getClazz(){
        return clazz;
    }
}
