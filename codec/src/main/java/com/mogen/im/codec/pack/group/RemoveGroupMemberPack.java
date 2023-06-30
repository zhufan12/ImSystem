package com.mogen.im.codec.pack.group;

import lombok.Data;


@Data
public class RemoveGroupMemberPack {

    private Integer groupId;

    private String member;

}
