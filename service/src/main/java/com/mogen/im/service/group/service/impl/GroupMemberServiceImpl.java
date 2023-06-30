package com.mogen.im.service.group.service.impl;

import com.mogen.im.common.ResponseVo;
import com.mogen.im.common.enums.GroupErrorCode;
import com.mogen.im.common.enums.GroupMemberRole;
import com.mogen.im.common.enums.GroupMuteType;
import com.mogen.im.common.enums.GroupStatus;
import com.mogen.im.common.exception.ApplicationException;
import com.mogen.im.common.utils.BeanUtils;
import com.mogen.im.service.group.entity.Group;
import com.mogen.im.service.group.entity.GroupMember;
import com.mogen.im.service.group.modle.req.*;
import com.mogen.im.service.group.modle.resp.AddMemberResp;
import com.mogen.im.service.group.modle.resp.GetRoleInGroupResp;
import com.mogen.im.service.group.repostiory.GroupMemberRepository;
import com.mogen.im.service.group.service.GroupMemberService;
import com.mogen.im.service.group.service.GroupService;
import com.mogen.im.service.user.entity.User;
import com.mogen.im.service.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class GroupMemberServiceImpl implements GroupMemberService {

    @Autowired
    private GroupMemberRepository groupMemberRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private GroupService groupService;

    @Autowired
    @Lazy
    private  GroupMemberService groupMemberService;


    @Override
    public ResponseVo importGroupMember(ImportGroupMemberReq req) {
        ResponseVo groupResp = groupService.getGroup(req.getGroupId());
        if(!groupResp.isOk()){
            return groupResp;
        }
        List<AddMemberResp> addMemberResps = new ArrayList<>();
        ResponseVo responseVo;
        for(GroupMemberDto member : req.getMembers()){
            try {
                responseVo = groupMemberService.addGroupMember(req.getGroupId(),req.getAppId(),member);
            }catch (Exception e){
                e.printStackTrace();
                responseVo = ResponseVo.errorResponse();
            }
            AddMemberResp addMemberResp = new AddMemberResp();
            addMemberResp.setUserId(member.getUserId());
            if (responseVo.isOk()) {
                addMemberResp.setResult(0);
            } else if (responseVo.getCode() == GroupErrorCode.USER_IS_JOINED_GROUP.getCode()) {
                addMemberResp.setResult(2);
            } else {
                addMemberResp.setResult(1);
            }
            addMemberResps.add(addMemberResp);
        }
        return ResponseVo.successResponse(addMemberResps);

    }

    @Override
    @Transactional
    public ResponseVo addGroupMember(Integer groupId, Integer appId, GroupMemberDto dto){
        ResponseVo userResp = userService.getSingleUserInfo(dto.getUserId(),appId);
        if(!userResp.isOk()){
            return userResp;
        }
        if(dto.getRole().equals(GroupMemberRole.OWNER)){
            long countOwner = groupMemberRepository.countByGroupIdAndRole(groupId,GroupMemberRole.OWNER);
            if(countOwner > 0){
                return ResponseVo.errorResponse(GroupErrorCode.GROUP_IS_HAVE_OWNER);
            }
        }


        Optional<GroupMember> groupMember = groupMemberRepository.findByAppIdAndUserIdAndGroupId(appId,dto.getUserId(),groupId);
        if(!groupMember.isPresent()){
            GroupMember groupMemberEntity = new GroupMember();
            BeanUtils.copyPropertiesIgnoreNull(dto,groupMemberEntity);
            groupMemberEntity.setJoinTime(System.currentTimeMillis());
            groupMemberEntity.setGroupId(groupId);
            groupMemberEntity.setAppId(appId);
            BeanUtils.copyPropertiesIgnoreNull(dto,groupMemberEntity);
            groupMemberEntity.setAppId(appId);
            groupMemberEntity.setGroupId(groupId);
            groupMemberEntity.setJoinTime(System.currentTimeMillis());
            GroupMember groupMemberInsert = groupMemberRepository.save(groupMemberEntity);
            if(groupMemberInsert == null){
                ResponseVo.errorResponse(GroupErrorCode.USER_JOIN_GROUP_ERROR);
            }
            return ResponseVo.successResponse();
        }

        if(groupMember.get().getRole().equals(GroupMemberRole.LEAVE)){
            GroupMember groupMemberEntity = new GroupMember();
            BeanUtils.copyPropertiesIgnoreNull(dto,groupMemberEntity);
            groupMemberEntity.setJoinTime(System.currentTimeMillis());
            groupMemberEntity.setGroupId(groupId);
            groupMemberEntity.setAppId(appId);
            groupMemberEntity.setId(groupMember.get().getId());
            GroupMember groupMemberUpdate = groupMemberRepository.save(groupMemberEntity);
            if(groupMemberUpdate == null){
                ResponseVo.errorResponse(GroupErrorCode.USER_JOIN_GROUP_ERROR);
            }
            return ResponseVo.successResponse();
        }


        return ResponseVo.errorResponse(GroupErrorCode.USER_IS_JOINED_GROUP);
    }

    @Override
    public ResponseVo getMemberJoinedGroup(GetJoinedGroupReq req) {
        List<GroupMember> groupMembers = groupMemberRepository.findByAppIdAndUserIdAndRoleNot(req.getAppId(),req.getUserId(),GroupMemberRole.OWNER);
        List<Integer> groupIds = groupMembers.stream().map(GroupMember::getGroupId).collect(Collectors.toList());
       return ResponseVo.successResponse(groupIds);
    }

    @Override
    public ResponseVo<GetRoleInGroupResp> getRoleInGroupOne(Integer groupId, String userId, Integer appId) {
        Optional<GroupMember> optionalGroupMember = groupMemberRepository.findByAppIdAndUserIdAndGroupId(appId,userId,groupId);
        if(!optionalGroupMember.isPresent()){
            return ResponseVo.errorResponse(GroupErrorCode.MEMBER_IS_NOT_JOINED_GROUP);
        }
        GetRoleInGroupResp getRoleInGroupResp = new GetRoleInGroupResp();
        BeanUtils.copyPropertiesIgnoreNull(optionalGroupMember.get(),getRoleInGroupResp);
        return ResponseVo.successResponse(getRoleInGroupResp);
    }

    @Override
    @Transactional
    public ResponseVo transferGroupMember(String owner, Integer groupId, Integer appId) {
      groupMemberRepository.updateRoleByAppIdAndGroupIdAndRole(GroupMemberRole.ORDINARY,appId,groupId,GroupMemberRole.OWNER);
      groupMemberRepository.updateRoleByUserIdAndAppId(GroupMemberRole.OWNER,owner,appId);
      return ResponseVo.successResponse();
    }

    @Override
    @Transactional
    public ResponseVo updateGroupMember(UpdateGroupMemberReq req) {
        ResponseVo<Group> group = groupService.getGroup(req.getGroupId());
        if (!group.isOk()) {
            return group;
        }

        Group groupData = group.getData();
        if (groupData.getStatus().equals(GroupStatus.DESTROY)) {
            throw new ApplicationException(GroupErrorCode.GROUP_IS_DESTROY);
        }
        boolean isMeOperate = req.getOperator().equals(req.getUserId());
        if((req.getAlias() != null && !req.getAlias().isEmpty()) && !isMeOperate){
            return ResponseVo.errorResponse(GroupErrorCode.THIS_OPERATE_NEED_ONESELF);
        }
        ResponseVo<GetRoleInGroupResp> roleInGroupOne = this.getRoleInGroupOne(req.getGroupId(), req.getUserId(), req.getAppId());
        if(!roleInGroupOne.isOk()){
            return roleInGroupOne;
        }
        ResponseVo<GetRoleInGroupResp> operateRoleInGroupOne = this.getRoleInGroupOne(req.getGroupId(), req.getOperator(), req.getAppId());
        if(!operateRoleInGroupOne.isOk()){
            return operateRoleInGroupOne;
        }

        if(req.getRole() != null){
            GetRoleInGroupResp data = operateRoleInGroupOne.getData();
            boolean isOwner = data.getRole().equals(GroupMemberRole.OWNER);
            boolean isManager = data.getRole().equals(GroupMemberRole.MANAGER);

            if(req.getRole() != null && !isOwner && !isManager){
                return ResponseVo.errorResponse(GroupErrorCode.THIS_OPERATE_NEED_MANAGER_ROLE);
            }

            if(req.getRole() != null && req.getRole().equals(GroupMemberRole.MANAGER) && !isOwner){
                return ResponseVo.errorResponse(GroupErrorCode.THIS_OPERATE_NEED_OWNER_ROLE);
            }
        }

        GroupMember groupMember = new GroupMember();
        BeanUtils.copyPropertiesIgnoreNull(groupMember,roleInGroupOne.getData());
        BeanUtils.copyPropertiesIgnoreNull(groupMember,req);
        groupMemberRepository.save(groupMember);
        return ResponseVo.successResponse();

    }


    @Override
    public ResponseVo addMember(AddGroupMemberReq req) {
        ResponseVo responseVo = groupService.getGroup(req.getGroupId());
        if(!responseVo.isOk()){
            return  responseVo;
        }
        List<AddMemberResp> addMemberResps = new ArrayList<>();
        ResponseVo resp;
        for(GroupMemberDto member : req.getMembers()) {
            try {
                resp = groupMemberService.addGroupMember(req.getGroupId(), req.getAppId(), member);
            } catch (Exception e) {
                e.printStackTrace();
                resp = ResponseVo.errorResponse();
            }
            AddMemberResp addMemberResp = new AddMemberResp();
            addMemberResp.setUserId(member.getUserId());
            if (resp.isOk()) {
                addMemberResp.setResult(0);
            } else if (resp.getCode() == GroupErrorCode.USER_IS_JOINED_GROUP.getCode()) {
                addMemberResp.setResult(2);
            } else {
                addMemberResp.setResult(1);
            }
            addMemberResps.add(addMemberResp);
        }
        return ResponseVo.successResponse(addMemberResps);

    }

    @Override
    public ResponseVo removeMember(RemoveGroupMemberReq req) {
        ResponseVo groupResp = groupService.getGroup(req.getGroupId());
        if(!groupResp.isOk()){
            return groupResp;
        }
        Group group = (Group)groupResp.getData();
        ResponseVo responseVo = getRoleInGroupOne(req.getGroupId(),req.getOperator(),req.getAppId());
        if(!responseVo.isOk()){
            return  responseVo;
        }
        GetRoleInGroupResp roleInGroup = (GetRoleInGroupResp)responseVo.getData();
        if(!roleInGroup.getRole().equals(GroupMemberRole.OWNER) || !roleInGroup.getRole().equals(GroupMemberRole.MANAGER)){
            throw new ApplicationException(GroupErrorCode.THIS_OPERATE_NEED_MANAGER_ROLE);
        }
        ResponseVo<GetRoleInGroupResp> roleInGroupOne = this.getRoleInGroupOne(req.getGroupId(), req.getMemberId(), req.getAppId());
        if (!roleInGroupOne.isOk()) {
            return roleInGroupOne;
        }
        GetRoleInGroupResp memberRole = roleInGroupOne.getData();
        if (memberRole.getRole().equals(GroupMemberRole.OWNER)) {
            throw new ApplicationException(GroupErrorCode.GROUP_OWNER_IS_NOT_REMOVE);
        }

        if (!memberRole.getRole().equals(GroupMemberRole.ORDINARY)) {
            throw new ApplicationException(GroupErrorCode.THIS_OPERATE_NEED_OWNER_ROLE);
        }
        return groupMemberService.removeGroupMember(req.getGroupId(),req.getAppId(),req.getMemberId());
    }


    @Override
    @Transactional
    public ResponseVo removeGroupMember(Integer groupId, Integer appId, String memberId) {
        ResponseVo<User> singleUserInfo = userService.getSingleUserInfo(memberId, appId);
        if(!singleUserInfo.isOk()){
            return singleUserInfo;
        }

        ResponseVo<GetRoleInGroupResp> roleInGroupOne = getRoleInGroupOne(groupId, memberId, appId);
        if (!roleInGroupOne.isOk()) {
            return roleInGroupOne;
        }
        GetRoleInGroupResp resp = roleInGroupOne.getData();
        groupMemberRepository.updateRoleAndLeaveTimeById(GroupMemberRole.LEAVE,System.currentTimeMillis(),resp.getId());
        return  ResponseVo.successResponse();
    }

    @Override
    public ResponseVo exitGroup(ExitGroupReq req) {
        ResponseVo groupResp = groupService.getGroup(req.getGroupId());
        if(!groupResp.isOk()){
            return groupResp;
        }

        ResponseVo<GetRoleInGroupResp> groupMember = groupMemberService.getRoleInGroupOne(req.getGroupId(),req.getMemberId(),req.getAppId());
        if(!groupMember.isOk()){
            return groupMember;
        }
        groupMemberRepository.updateRoleAndLeaveTimeById(GroupMemberRole.LEAVE,System.currentTimeMillis(),groupMember.getData().getId());
        return ResponseVo.successResponse();
    }

    @Override
    public ResponseVo speak(SpeakMemberReq req) {
        ResponseVo<Group> groupResp = groupService.getGroup(req.getGroupId());
        if (!groupResp.isOk()) {
            return groupResp;
        }
        GetRoleInGroupResp memberRole = null;
        boolean isManager = false;
        boolean isOwner = false;
        ResponseVo<GetRoleInGroupResp> role = getRoleInGroupOne(req.getGroupId(), req.getOperator(), req.getAppId());
        if (!role.isOk()) {
            return role;
        }

        GetRoleInGroupResp data = role.getData();

        isOwner = data.getRole().equals(GroupMemberRole.OWNER);
        isManager = data.getRole().equals(GroupMemberRole.MANAGER);

        if (!isOwner && !isManager) {
            throw new ApplicationException(GroupErrorCode.THIS_OPERATE_NEED_MANAGER_ROLE);
        }

        ResponseVo<GetRoleInGroupResp> roleInGroupOne = this.getRoleInGroupOne(req.getGroupId(), req.getMemberId(), req.getAppId());
        if (!roleInGroupOne.isOk()) {
            return roleInGroupOne;
        }
        memberRole = roleInGroupOne.getData();
        if (isManager && memberRole.getRole().equals(GroupMemberRole.OWNER)) {
            throw new ApplicationException(GroupErrorCode.THIS_OPERATE_NEED_OWNER_ROLE);
        }
        int update;
        if(req.getSpeakDate() > 0){
            update = groupMemberRepository.updateSpeakDateById(System.currentTimeMillis() +req.getSpeakDate(),memberRole.getId());
        }else {
            update = groupMemberRepository.updateSpeakDateById(req.getSpeakDate(),memberRole.getId());
        }
        if(update == 0){
            return ResponseVo.errorResponse();
        }
        return ResponseVo.successResponse();

    }

    @Override
    public List<String> getGroupMemberId(Integer groupId) {
        return groupMemberRepository.findByGroupMemberByGroupId(groupId);
    }

    @Override
    public List<GroupMember> getGroupManager(Integer groupId) {
       return groupMemberRepository.findByGroupIdAndRoleIn(groupId,
               Arrays.asList(GroupMemberRole.OWNER,GroupMemberRole.MANAGER));
    }

    @Override
    public ResponseVo<List<Integer>> syncMemberJoinedGroup(String memberId, Integer appId) {
        List<Integer> groupIds = groupMemberRepository.findGroupIdByMemberIdAndAppIdAndStatusNot(memberId,appId,GroupMemberRole.LEAVE);
        return ResponseVo.successResponse(groupIds);
    }
}
