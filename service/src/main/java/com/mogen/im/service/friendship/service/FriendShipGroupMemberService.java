package com.mogen.im.service.friendship.service;

import com.mogen.im.common.ResponseVo;
import com.mogen.im.service.friendship.entity.FriendShipGroupMember;
import com.mogen.im.service.friendship.model.req.FriendShipGroupMemberReq;

public interface FriendShipGroupMemberService {

    public ResponseVo addGroupMember(FriendShipGroupMemberReq req);

    public ResponseVo delGroupMember(FriendShipGroupMemberReq req);

    public FriendShipGroupMember doAddGroupMember(Integer groupId, String toId);

    public int clearGroupMember(Integer groupId);
}
