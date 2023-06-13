package com.mogen.im.service.group.modle.req;

import com.mogen.im.common.enums.GroupMuteType;
import com.mogen.im.common.enums.GroupType;
import com.mogen.im.common.model.RequestBase;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ImportGroupReq extends RequestBase {


    private String ownerId;


    private GroupType groupType;

    @NotBlank(message = "group name can't be null")
    private String groupName;

    private GroupMuteType mute = GroupMuteType.NOT_MUTE;

    private Integer applyJoinType;

    private String introduction;

    private String notification;

    private String photo;

    private Integer MaxMemberCount;

    private Long createTime;

    private String extra;

}
