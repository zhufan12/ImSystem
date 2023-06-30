package com.mogen.im.tcp.register;

import com.mogen.im.common.constants.Constants;

public class ZKit {

    private static ZookeeperkClient zkClient;


    public ZKit(ZookeeperkClient zkClient) {
        this.zkClient = zkClient;
    }

    public void createRootNode(){
        if(!zkClient.exists(Constants.ImCoreZkRoot)){
            zkClient.createPersistentNode(Constants.ImCoreZkRoot);
        }

        if(!zkClient.exists(Constants.ImCoreZkRoot +  Constants.ImCoreZkRootTcp)){
            zkClient.createPersistentNode(Constants.ImCoreZkRoot +  Constants.ImCoreZkRootTcp);
        }

        if(!zkClient.exists(Constants.ImCoreZkRoot +  Constants.ImCoreZkRootWeb)){
            zkClient.createPersistentNode(Constants.ImCoreZkRoot +  Constants.ImCoreZkRootTcp);
        }
    }


    public void createNode(String path) {
        if(!zkClient.exists(path)){
            zkClient.createPersistentNode(path);
        }
    }
}
