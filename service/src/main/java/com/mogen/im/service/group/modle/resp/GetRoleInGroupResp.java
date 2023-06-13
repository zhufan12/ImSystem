package com.mogen.im.service.group.modle.resp;

import com.mogen.im.common.enums.GroupMemberRole;
import lombok.Data;

@Data
public class GetRoleInGroupResp {

    private Integer id;

    private String userId;

    private GroupMemberRole role;

    private Long speakDate;

}