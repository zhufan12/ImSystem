package com.mogen.im.service.group.modle.req;

import com.mogen.im.common.model.RequestBase;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SpeakMemberReq  extends RequestBase {

    @NotNull(message = "group id can;t be null")
    private Integer groupId;

    @NotBlank(message = "memberId can;t be null")
    private String memberId;

    //禁言时间，单位毫秒
    @NotNull(message = "speak date can;t be null")
    private Long speakDate;
}
