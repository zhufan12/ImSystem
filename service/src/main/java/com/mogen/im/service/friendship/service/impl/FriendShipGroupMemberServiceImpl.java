package com.mogen.im.service.friendship.service.impl;

import com.mogen.im.codec.pack.friend.AddFriendGroupMemberPack;
import com.mogen.im.codec.pack.friend.DeleteFriendGroupMemberPack;
import com.mogen.im.common.ResponseVo;
import com.mogen.im.common.enums.action.FriendshipEventAction;
import com.mogen.im.common.model.ClientInfo;
import com.mogen.im.service.friendship.entity.FriendShipGroup;
import com.mogen.im.service.friendship.entity.FriendShipGroupMember;
import com.mogen.im.service.friendship.model.req.FriendShipGroupMemberReq;
import com.mogen.im.service.friendship.repository.FriendShipGroupMemberRepository;
import com.mogen.im.service.friendship.service.FriendShipGroupMemberService;
import com.mogen.im.service.friendship.service.FriendShipGroupService;
import com.mogen.im.service.user.entity.User;
import com.mogen.im.service.user.service.UserService;
import com.mogen.im.service.utils.MessageProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class FriendShipGroupMemberServiceImpl implements FriendShipGroupMemberService {

    @Autowired
    private FriendShipGroupMemberRepository friendShipGroupMemberRepository;

    @Autowired
    @Lazy
    private FriendShipGroupService friendShipGroupService;

    @Autowired
    private UserService userService;

    @Autowired
    MessageProducer messageProducer;


    @Override
    public ResponseVo addGroupMember(FriendShipGroupMemberReq req) {
        ResponseVo group = friendShipGroupService.getGroup(req.getFromId(),req.getGroupName(),req.getAppId());
        if(!group.isOk()){
            return group;
        }

        List<String> successId = new ArrayList<>();
        FriendShipGroup friendShipGroup = (FriendShipGroup)group.getData();
        for (String toId : req.getToIds()) {
            ResponseVo<User> singleUserInfo = userService.getSingleUserInfo(toId, req.getAppId());
            if(singleUserInfo.isOk()){
                FriendShipGroupMember update = doAddGroupMember(friendShipGroup.getId(), toId);
                if(update != null){
                    successId.add(toId);
                }
            }
        }
        AddFriendGroupMemberPack pack = new AddFriendGroupMemberPack();
        pack.setFromId(req.getFromId());
        pack.setGroupName(req.getGroupName());
        pack.setToIds(successId);
        messageProducer.sendToUserExceptClient(req.getFromId(), FriendshipEventAction.FRIEND_GROUP_MEMBER_ADD,
                pack,new ClientInfo(req.getAppId(),req.getClientType(),req.getImei()));
        return ResponseVo.successResponse(successId);
    }

    @Override
    @Transactional
    public ResponseVo delGroupMember(FriendShipGroupMemberReq req) {
        ResponseVo group = friendShipGroupService.getGroup(req.getFromId(),req.getGroupName(),req.getAppId());
        if(!group.isOk()){
            return group;
        }

        List<String> successId = new ArrayList<>();
        FriendShipGroup friendShipGroup = (FriendShipGroup)group.getData();
        for (String toId : req.getToIds()) {
            ResponseVo<User> singleUserInfo = userService.getSingleUserInfo(toId, req.getAppId());
            if(singleUserInfo.isOk()){
                int i = deleteGroupMember(friendShipGroup.getId(), req.getToIds());
                if(i != -1){
                    successId.add(toId);
                    DeleteFriendGroupMemberPack pack = new DeleteFriendGroupMemberPack();
                    pack.setFromId(req.getFromId());
                    pack.setGroupName(req.getGroupName());
                    pack.setToIds(successId);
                    messageProducer.sendToUser(req.getFromId(), FriendshipEventAction.FRIEND_GROUP_MEMBER_DELETE,
                            pack,new ClientInfo(req.getAppId(),req.getClientType(),req.getImei()));
                }
            }
        }
        return ResponseVo.successResponse(successId);
    }

    @Override
    public FriendShipGroupMember doAddGroupMember(Integer groupId, String toId) {
        FriendShipGroupMember friendShipGroupMember = new FriendShipGroupMember();
        friendShipGroupMember.setGroupId(groupId);
        friendShipGroupMember.setToId(toId);
        return friendShipGroupMemberRepository.save(friendShipGroupMember);
    }

    public int deleteGroupMember(Integer groupId, List<String> toIds) {
        return friendShipGroupMemberRepository.deleteByGroupIdAndToIdIn(groupId,toIds);
    }
    @Override
    public int clearGroupMember(Integer groupId) {
        return friendShipGroupMemberRepository.deleteByGroupId(groupId);
    }
}
