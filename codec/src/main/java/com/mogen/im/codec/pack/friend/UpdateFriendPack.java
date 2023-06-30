package com.mogen.im.codec.pack.friend;

import lombok.Data;

@Data
public class UpdateFriendPack {

    public String fromId;

    private String toId;

    private String remark;

    private Long sequence;
}
