package com.mogen.im.service.friendship.service.impl;

import com.mogen.im.codec.pack.friend.AddFriendGroupPack;
import com.mogen.im.codec.pack.friend.DeleteFriendGroupPack;
import com.mogen.im.common.ResponseVo;
import com.mogen.im.common.constants.Constants;
import com.mogen.im.common.enums.DelFlag;
import com.mogen.im.common.enums.FriendShipErrorCode;
import com.mogen.im.common.enums.action.FriendshipEventAction;
import com.mogen.im.common.model.ClientInfo;
import com.mogen.im.service.friendship.entity.FriendShipGroup;
import com.mogen.im.service.friendship.model.req.AddFriendShipGroupReq;
import com.mogen.im.service.friendship.model.req.DeleteFriendShipGroupReq;
import com.mogen.im.service.friendship.model.req.FriendShipGroupMemberReq;
import com.mogen.im.service.friendship.repository.FriendShipGroupRepository;
import com.mogen.im.service.friendship.service.FriendShipGroupMemberService;
import com.mogen.im.service.friendship.service.FriendShipGroupService;
import com.mogen.im.service.seq.RedisSeq;
import com.mogen.im.service.user.service.UserService;
import com.mogen.im.service.utils.MessageProducer;
import com.mogen.im.service.utils.WriteUserSeq;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class FriendShipGroupServiceImpl implements FriendShipGroupService {

    @Autowired
    private FriendShipGroupRepository friendShipGroupRepository;

    @Autowired
    private FriendShipGroupMemberService friendShipGroupMemberService;

    @Autowired
    private MessageProducer messageProducer;

    @Autowired
    private RedisSeq redisSeq;

    @Autowired
    private WriteUserSeq writeUserSeq;

    @Override
    public ResponseVo addGroup(AddFriendShipGroupReq req) {
        Optional<FriendShipGroup> friendShipGroup =  friendShipGroupRepository.findByAppIdAndFromIdAndGroupName(req.getAppId(),req.getFromId(),req.getGroupName());
        if(friendShipGroup.isPresent()){
            return ResponseVo.errorResponse(FriendShipErrorCode.FRIEND_SHIP_GROUP_IS_EXIST);
        }
        long seq = redisSeq.doGetSeq(req.getAppId() + ":" + Constants.SeqConstants.FriendshipGroup);
        FriendShipGroup friendShipGroupEntity = new FriendShipGroup();
        BeanUtils.copyProperties(req,friendShipGroupEntity);
        friendShipGroupEntity.setSequence(seq);
        FriendShipGroup insert = friendShipGroupRepository.save(friendShipGroupEntity);
        if(insert == null){
            return ResponseVo.errorResponse(FriendShipErrorCode.FRIEND_SHIP_GROUP_CREATE_ERROR);
        }
        writeUserSeq.writeUserSeq(req.getAppId(),req.getFromId(),Constants.SeqConstants.FriendshipGroup,seq);

        if(!req.getToIds().isEmpty()){
            FriendShipGroupMemberReq friendShipGroupMemberReq = new FriendShipGroupMemberReq();
            friendShipGroupMemberReq.setFromId(req.getFromId());
            friendShipGroupMemberReq.setGroupName(req.getGroupName());
            friendShipGroupMemberReq.setToIds(req.getToIds());
            friendShipGroupMemberReq.setAppId(req.getAppId());
            friendShipGroupMemberService.addGroupMember(friendShipGroupMemberReq);
        }
        AddFriendGroupPack addFriendGropPack = new AddFriendGroupPack();
        addFriendGropPack.setFromId(req.getFromId());
        addFriendGropPack.setSequence(seq);
        addFriendGropPack.setGroupName(req.getGroupName());
        messageProducer.sendToUserExceptClient(req.getFromId(), FriendshipEventAction.FRIEND_GROUP_ADD,
                addFriendGropPack,new ClientInfo(req.getAppId(),req.getClientType(),req.getImei()));

        return ResponseVo.successResponse();

    }

    @Override
    @Transactional
    public ResponseVo deleteGroup(DeleteFriendShipGroupReq req) {
        req.getGroupName().forEach(item -> {
            Optional<FriendShipGroup> friendShipGroup =  friendShipGroupRepository.findByAppIdAndFromIdAndGroupName(req.getAppId(),req.getFromId(),item);
            long seq = redisSeq.doGetSeq(req.getAppId() + ":" + Constants.SeqConstants.FriendshipGroup);
            if(friendShipGroup.isPresent()) {
                FriendShipGroup friendShipGroup1 = friendShipGroupRepository.updateDelFlagByFromIdAndAppIdAndGroupName(DelFlag.DELETE, req.getFromId(), req.getAppId(), item,seq);
                friendShipGroupMemberService.clearGroupMember(friendShipGroup1.getId());
            }
            DeleteFriendGroupPack deleteFriendGroupPack = new DeleteFriendGroupPack();
            deleteFriendGroupPack.setFromId(req.getFromId());
            deleteFriendGroupPack.setGroupName(item);
            deleteFriendGroupPack.setSequence(seq);
            writeUserSeq.writeUserSeq(req.getAppId(),req.getFromId(),Constants.SeqConstants.FriendshipGroup,seq);
            messageProducer.sendToUserExceptClient(req.getFromId(), FriendshipEventAction.FRIEND_GROUP_DELETE,
                    deleteFriendGroupPack,new ClientInfo(req.getAppId(),req.getClientType(),req.getImei()));
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
