package com.mogen.im.codec.pack.friend;

import lombok.Data;

import java.util.List;


@Data
public class DeleteFriendGroupMemberPack {

    public String fromId;

    private String groupName;

    private List<String> toIds;

    private Long sequence;
}
