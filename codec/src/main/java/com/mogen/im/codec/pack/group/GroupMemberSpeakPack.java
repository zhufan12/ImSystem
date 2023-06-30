package com.mogen.im.codec.pack.group;

import lombok.Data;

@Data
public class GroupMemberSpeakPack {

    private Integer groupId;

    private String memberId;

    private Long speakDate;

}
