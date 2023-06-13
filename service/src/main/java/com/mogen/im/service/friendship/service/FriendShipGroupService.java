package com.mogen.im.service.friendship.service;

import com.mogen.im.common.ResponseVo;
import com.mogen.im.service.friendship.entity.FriendShipGroup;
import com.mogen.im.service.friendship.model.req.AddFriendShipGroupReq;
import com.mogen.im.service.friendship.model.req.DeleteFriendShipGroupReq;

public interface FriendShipGroupService {

    public ResponseVo addGroup(AddFriendShipGroupReq req);

    public ResponseVo deleteGroup(DeleteFriendShipGroupReq req);

    public ResponseVo<FriendShipGroup> getGroup(String fromId, String groupName, Integer appId);

    public Long updateSeq(String fromId, String groupName, Integer appId);
}
