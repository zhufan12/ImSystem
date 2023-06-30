package com.mogen.im.service.message.service;

import com.mogen.im.common.ResponseVo;
import com.mogen.im.common.enums.*;
import com.mogen.im.service.friendship.entity.FriendShip;
import com.mogen.im.service.friendship.model.req.GetRelationReq;
import com.mogen.im.service.friendship.service.FriendShipService;
import com.mogen.im.service.group.entity.Group;
import com.mogen.im.service.group.entity.GroupMember;
import com.mogen.im.service.group.service.GroupMemberService;
import com.mogen.im.service.group.service.GroupService;
import com.mogen.im.service.user.entity.User;
import com.mogen.im.service.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CheckSendMessageService {


    @Autowired
    private UserService userService;

    @Autowired
    private FriendShipService friendShipService;

    @Autowired
    private GroupService groupService;

    @Autowired
    private GroupMemberService groupMemberService;

    public ResponseVo checkSenderForvidAndMute(String fromId,Integer appId){
        ResponseVo responseVo = userService.getSingleUserInfo(fromId,appId);
        if(!responseVo.isOk()){
            return responseVo;
        }
        User user = (User) responseVo.getData();
        if(user.getForbiddenFlag().equals(UserForbiddenFlagType.FORBIBBEN)){
            return ResponseVo.errorResponse(MessageErrorCode.FORMER_IS_FORBIDDEN);
        }else if(user.getSilentFlag().equals(UserSilentFlagType.MUTE)){
            return ResponseVo.errorResponse(MessageErrorCode.FORMER_IS_MUTE);
        }
        return ResponseVo.successResponse();
    }


    public ResponseVo checkFriendShip(String fromId,String toId,Integer appId){
        GetRelationReq fromRelationReq = new GetRelationReq();
        fromRelationReq.setFromId(fromId);
        fromRelationReq.setToId(toId);
        fromRelationReq.setAppId(appId);
        ResponseVo<FriendShip> fromRelationResp =  friendShipService.getRelation(fromRelationReq);
        if(!fromRelationResp.isOk()){
            return fromRelationResp;
        }
        GetRelationReq toRelationReq = new GetRelationReq();
        toRelationReq.setFromId(toId);
        toRelationReq.setToId(fromId);
        toRelationReq.setAppId(appId);
        ResponseVo<FriendShip> toRelationResp = friendShipService.getRelation(toRelationReq);
        if(!fromRelationResp.isOk()){
            return fromRelationResp;
        }
        if(!FriendShipStatus.FRIEND_STATUS_NORMAL.equals(fromRelationResp.getData().getStatus())){
            return ResponseVo.errorResponse(FriendShipErrorCode.FRIEND_IS_DELETED);
        }

        if(!FriendShipStatus.FRIEND_STATUS_NORMAL.equals(toRelationResp.getData().getStatus())){
            return ResponseVo.errorResponse(FriendShipErrorCode.FRIEND_IS_DELETED);
        }

        if(!FriendShipStatus.BLACK_STATUS_BLACKED.equals(fromRelationResp.getData().getBlack())){
            return ResponseVo.errorResponse(FriendShipErrorCode.FRIEND_IS_BLACK);
        }

        if(!FriendShipStatus.BLACK_STATUS_BLACKED.equals(toRelationResp.getData().getBlack())){
            return ResponseVo.errorResponse(FriendShipErrorCode.TARGET_IS_BLACK_YOU);
        }


        return ResponseVo.successResponse();
    }


    public ResponseVo checkGroupMessage(String fromId,Integer groupId,Integer appId){
        ResponseVo checkUser = checkSenderForvidAndMute(fromId,appId);
        if (!checkUser.isOk()){
            return checkUser;
        }

        ResponseVo<Group> groupResp =  groupService.getGroup(groupId);
        if(!groupResp.isOk()){
            return groupResp;
        }

        ResponseVo memberCheck = groupMemberService.getRoleInGroupOne(groupId,fromId,appId);
        if(!memberCheck.isOk()){
            return  memberCheck;
        }
        GroupMember groupMember = (GroupMember)memberCheck.getData();
        if(groupResp.getData().getMute().equals(GroupMuteType.MUTE) &&
                !(groupMember.getRole().equals(GroupMemberRole.OWNER) ||
                        groupMember.getRole().equals(GroupMemberRole.MANAGER))){
            return ResponseVo.errorResponse(GroupErrorCode.THIS_GROUP_IS_MUTE);
        }
        if(groupMember.getSpeakDate() != null && groupMember.getSpeakDate() > System.currentTimeMillis()){
            return ResponseVo.errorResponse(GroupErrorCode.GROUP_MEMBER_IS_SPEAK);
        }

        return ResponseVo.successResponse();
    }
}
