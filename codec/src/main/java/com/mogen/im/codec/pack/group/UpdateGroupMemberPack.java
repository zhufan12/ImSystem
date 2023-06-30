package com.mogen.im.codec.pack.group;

import lombok.Data;

@Data
public class UpdateGroupMemberPack {

    private Integer groupId;

    private String memberId;

    private String alias;

    private String extra;
}
