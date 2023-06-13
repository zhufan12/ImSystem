package com.mogen.im.service.user.service;

import com.mogen.im.common.ResponseVo;
import com.mogen.im.service.user.model.req.*;

public interface UserService {

    public ResponseVo importUsers(ImportUserReq importUserReq);

    public ResponseVo getUserInfo(GetUserInfoReq req);

    public ResponseVo getSingleUserInfo(String userId , Integer appId);

    public ResponseVo deleteUser(DeleteUserReq req);

    public ResponseVo modifyUserInfo(ModifyUserInfoReq req);

    public ResponseVo login(LoginReq req);

    ResponseVo getUserSequence(GetUserSequenceReq req);
}
