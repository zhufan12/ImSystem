package com.mogen.im.service.group.modle.req;

import com.mogen.im.common.enums.GroupMemberRole;
import lombok.Data;

@Data
public class GroupMemberDto {

    private String userId;

    private String alias;

    private GroupMemberRole role = GroupMemberRole.ORDINARY;

    private Long speakDate;

    private String joinType;

    private Long joinTime;
}
