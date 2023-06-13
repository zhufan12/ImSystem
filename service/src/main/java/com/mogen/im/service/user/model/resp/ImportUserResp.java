package com.mogen.im.service.user.model.resp;

import com.mogen.im.service.user.entity.User;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ImportUserResp {
    private List<String> successUsers;

    private List<String> failedUsers;
}
