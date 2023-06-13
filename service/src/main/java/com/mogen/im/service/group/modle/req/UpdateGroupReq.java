package com.mogen.im.service.group.modle.req;

import com.mogen.im.common.enums.GroupMuteType;
import com.mogen.im.common.model.RequestBase;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateGroupReq extends RequestBase {

    @NotBlank(message = "group Id not null")
    private Integer id;

    private String groupName;

    private GroupMuteType mute;

    private Integer applyJoinType;

    private String introduction;

    private String notification;

    private String photo;

    private Integer maxMemberCount;

    private String extra;

}
