package com.mogen.im.service.friendship.service;

import com.mogen.im.common.ResponseVo;
import com.mogen.im.service.friendship.model.req.ApprovedFriendRequestReq;
import com.mogen.im.service.friendship.model.req.FriendDto;
import com.mogen.im.service.friendship.model.req.GetFriendShipRequestReq;

public interface FriendShipRequestService {

    public ResponseVo addFriendShipRequest(String fromId, FriendDto dto, Integer appId);

    public ResponseVo approvedFriendRequest(ApprovedFriendRequestReq req);

    public ResponseVo readFriendShipRequestReq(GetFriendShipRequestReq req);

    public ResponseVo getFriendRequest(String fromId, Integer appId);
}
