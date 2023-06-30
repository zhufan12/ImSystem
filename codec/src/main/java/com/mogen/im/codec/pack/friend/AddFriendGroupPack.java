package com.mogen.im.codec.pack.friend;

import lombok.Data;
@Data
public class AddFriendGroupPack {
    public String fromId;

    private String groupName;

    private Long sequence;
}
