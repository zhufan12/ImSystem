package com.mogen.im.service.friendship.service.impl;

import com.mogen.im.common.ResponseVo;
import com.mogen.im.common.enums.DelFlagEnum;
import com.mogen.im.common.enums.FriendShipErrorCode;
import com.mogen.im.service.friendship.entity.FriendShipGroup;
import com.mogen.im.service.friendship.model.req.AddFriendShipGroupReq;
import com.mogen.im.service.friendship.model.req.DeleteFriendShipGroupReq;
import com.mogen.im.service.friendship.model.req.FriendShipGroupMemberReq;
import com.mogen.im.service.friendship.repository.FriendShipGroupRepository;
import com.mogen.im.service.friendship.service.FriendShipGroupMemberService;
import com.mogen.im.service.friendship.service.FriendShipGroupService;
import com.mogen.im.service.user.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class FriendShipGroupServiceImpl implements FriendShipGroupService {


    @Autowired
    private UserService userService;

    @Autowired
    private FriendShipGroupRepository friendShipGroupRepository;

    @Autowired
    private FriendShipGroupMemberService friendShipGroupMemberService;

    @Override
    public ResponseVo addGroup(AddFriendShipGroupReq req) {
        Optional<FriendShipGroup> friendShipGroup =  friendShipGroupRepository.findByAppIdAndFromIdAndGroupName(req.getAppId(),req.getFromId(),req.getGroupName());
        if(friendShipGroup.isPresent()){
            return ResponseVo.errorResponse(FriendShipErrorCode.FRIEND_SHIP_GROUP_IS_EXIST);
        }
        FriendShipGroup friendShipGroupEntity = new FriendShipGroup();
        BeanUtils.copyProperties(req,friendShipGroupEntity);

        FriendShipGroup insert = friendShipGroupRepository.save(friendShipGroupEntity);
        if(insert == null){
            return ResponseVo.errorResponse(FriendShipErrorCode.FRIEND_SHIP_GROUP_CREATE_ERROR);
        }

        if(!req.getToIds().isEmpty()){
            FriendShipGroupMemberReq friendShipGroupMemberReq = new FriendShipGroupMemberReq();
            friendShipGroupMemberReq.setFromId(req.getFromId());
            friendShipGroupMemberReq.setGroupName(req.getGroupName());
            friendShipGroupMemberReq.setToIds(req.getToIds());
            friendShipGroupMemberReq.setAppId(req.getAppId());
            friendShipGroupMemberService.addGroupMember(friendShipGroupMemberReq);
            return ResponseVo.successResponse();
        }

        return ResponseVo.successResponse();



    }

    @Override
    @Transactional
    public ResponseVo deleteGroup(DeleteFriendShipGroupReq req) {

        req.getGroupName().forEach(item -> {
            Optional<FriendShipGroup> friendShipGroup =  friendShipGroupRepository.findByAppIdAndFromIdAndGroupName(req.getAppId(),req.getFromId(),item);
            if(friendShipGroup.isPresent()) {
                FriendShipGroup friendShipGroup1 = friendShipGroupRepository.updateDelFlagByFromIdAndAppIdAndGroupName(DelFlagEnum.DELETE, req.getFromId(), req.getAppId(), item);
                friendShipGroupMemberService.clearGroupMember(friendShipGroup1.getId());
            }
        });

        return ResponseVo.successResponse();
    }

    @Override
    public ResponseVo<FriendShipGroup> getGroup(String fromId, String groupName, Integer appId) {
        Optional<FriendShipGroup> friendShipGroup =  friendShipGroupRepository.findByAppIdAndFromIdAndGroupName(appId,fromId,groupName);
        if (!friendShipGroup.isPresent()) {
            return ResponseVo.errorResponse(FriendShipErrorCode.FRIEND_SHIP_GROUP_IS_NOT_EXIST);
        }
        return ResponseVo.successResponse(friendShipGroup.get());
    }

    @Override
    public Long updateSeq(String fromId, String groupName, Integer appId) {
        return null;
    }
}
