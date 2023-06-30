package com.mogen.im.common.model;

import lombok.Data;

@Data
public class SyncReq extends RequestBase{

    private Long lastSequence;
    private Integer maxLimit;
}
