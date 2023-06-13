package com.mogen.im.service.group.modle.resp;

import lombok.Data;

@Data
public class AddMemberResp {

    private String userId;

    private Integer result;

    private String resultMessage;
}
