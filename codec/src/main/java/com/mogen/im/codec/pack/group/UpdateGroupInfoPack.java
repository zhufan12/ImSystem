package com.mogen.im.codec.pack.group;

import lombok.Data;

@Data
public class UpdateGroupInfoPack {

    private Integer groupId;

    private String groupName;

    private Integer mute;

    private Integer joinType;

    private String introduction;

    private String notification;

    private String photo;

    private Integer maxMemberCount;

    private Long sequence;
}
