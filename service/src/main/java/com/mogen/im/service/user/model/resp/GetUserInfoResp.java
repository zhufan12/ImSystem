package com.mogen.im.service.user.model.resp;

import com.mogen.im.service.user.entity.User;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
public class GetUserInfoResp {

    private List<User> userDataItem;

    private List<String> failUser;
}
