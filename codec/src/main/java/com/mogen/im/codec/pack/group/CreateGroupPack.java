package com.mogen.im.codec.pack.group;

import lombok.Data;

@Data
public class CreateGroupPack {

    private Integer groupId;

    private Integer appId;

    private String ownerId;

    private Integer groupType;

    private String groupName;

    private Integer mute;

    private Integer applyJoinType;

    private Integer privateChat;

    private String introduction;

    private String notification;

    private String photo;

    private Integer status;

    private Long sequence;

    private Long createTime;

    private String extra;

}
