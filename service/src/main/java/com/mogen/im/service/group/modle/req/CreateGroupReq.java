package com.mogen.im.service.group.modle.req;

import com.mogen.im.common.enums.GroupMuteType;
import com.mogen.im.common.enums.GroupType;
import com.mogen.im.common.model.RequestBase;
import lombok.Data;

import java.util.List;

@Data
public class CreateGroupReq extends RequestBase {

    private String ownerId;

    private GroupType groupType;

    private String groupName;

    private GroupMuteType mute;

    private Integer applyJoinType;

    private String introduction;

    private String notification;

    private String photo;

    private Integer MaxMemberCount;

    private List<GroupMemberDto> member;

    private String extra;

}
