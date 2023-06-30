package com.mogen.im.service.utils;

import com.mogen.im.common.constants.Constants;
import org.apache.curator.framework.CuratorFramework;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ZKit {

    private static Logger logger = LoggerFactory.getLogger(ZKit.class);

    @Autowired
    private CuratorFramework zkClient;


    public List<String> getAllTcpNode() throws Exception {
        List<String> children = zkClient.getChildren().forPath(Constants.ImCoreZkRoot + Constants.ImCoreZkRootTcp)
                .stream().toList();
        logger.info("Query all node =[{}] success.",children.toArray());
        return children;
    }

    public List<String> getAllWebNode() throws Exception {
        List<String> children = zkClient.getChildren().forPath(Constants.ImCoreZkRoot + Constants.ImCoreZkRootWeb)
                .stream().toList();
        logger.info("Query all node =[{}] success.",children.toArray());
        return children;
    }
}
