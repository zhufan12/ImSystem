package com.mogen.im.codec.pack.friend;


import lombok.Data;

@Data
public class AddFriendPack{
    private String fromId;
    private String remark;
    private String toId;
    private String addSource;
    private String addWording;
    private Long sequence;
}
