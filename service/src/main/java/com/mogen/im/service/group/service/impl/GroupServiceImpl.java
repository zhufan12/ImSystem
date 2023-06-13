package com.mogen.im.service.group.service.impl;

import com.mogen.im.common.ResponseVo;
import com.mogen.im.common.enums.GroupErrorCode;
import com.mogen.im.common.enums.GroupMemberRole;
import com.mogen.im.common.enums.GroupStatus;
import com.mogen.im.common.enums.GroupType;
import com.mogen.im.common.exception.ApplicationException;
import com.mogen.im.common.utils.BeanUtils;
import com.mogen.im.service.group.entity.Group;
import com.mogen.im.service.group.modle.req.*;
import com.mogen.im.service.group.modle.resp.GetRoleInGroupResp;
import com.mogen.im.service.group.repostiory.GroupRepository;
import com.mogen.im.service.group.service.GroupMemberService;
import com.mogen.im.service.group.service.GroupService;
import com.mogen.im.service.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class GroupServiceImpl implements GroupService {

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private UserService userService;

    @Autowired
    @Lazy
    private GroupMemberService groupMemberService;

    @Override
    public ResponseVo importGroup(ImportGroupReq req) {
        Group group = new Group();
        BeanUtils.copyPropertiesIgnoreNull(req,group);
        if(group.getGroupType().equals(GroupType.PUBLIC) && group.getOwnerId() == null){
            throw new ApplicationException(GroupErrorCode.PUBLIC_GROUP_MUST_HAVE_OWNER);
        }
        if (group.getCreateTime() == null) {
            group.setCreateTime(System.currentTimeMillis());
        }
        Group insertEntity = groupRepository.save(group);
        if (insertEntity == null){
            return ResponseVo.errorResponse(GroupErrorCode.IMPORT_GROUP_ERROR);
        }
        return ResponseVo.successResponse();

    }

    @Override
    public ResponseVo getGroup(Integer groupId) {
        Optional<Group> group = groupRepository.findById(groupId);
        if(group.isPresent()){
            return ResponseVo.successResponse(group.get());
        }
        return ResponseVo.errorResponse(GroupErrorCode.GROUP_IS_NOT_EXIST);
    }

    @Override
    @Transactional
    public ResponseVo createGroup(CreateGroupReq req) {
        Group group = new Group();
        BeanUtils.copyPropertiesIgnoreNull(req,group);
        Group create = groupRepository.save(group);

        GroupMemberDto groupMemberDto = new GroupMemberDto();
        groupMemberDto.setUserId(req.getOwnerId());
        groupMemberDto.setRole(GroupMemberRole.OWNER);
        groupMemberDto.setJoinTime(System.currentTimeMillis());
        groupMemberService.addGroupMember(create.getId(), req.getAppId(), groupMemberDto);
        return ResponseVo.successResponse();
    }

    @Override
    @Transactional
    public ResponseVo updateBaseGroupInfo(UpdateGroupReq req) {
        ResponseVo responseVo = getGroup(req.getId());
        if(!responseVo.isOk()){
            return  responseVo;
        }
        Group group = (Group) responseVo.getData();

        if(group.getStatus().equals(GroupStatus.DESTROY)){
            throw new ApplicationException(GroupErrorCode.GROUP_IS_DESTROY);
        }


        ResponseVo<GetRoleInGroupResp> role = groupMemberService.getRoleInGroupOne(req.getId(), req.getOperator(), req.getAppId());

        if (!role.isOk()) {
            return role;
        }

        GetRoleInGroupResp data = role.getData();
        GroupMemberRole roleInfo = data.getRole();

        boolean isManager = roleInfo.equals(GroupMemberRole.MANAGER) || roleInfo.equals(GroupMemberRole.OWNER);

        if (!isManager && group.getGroupType().equals(GroupType.PUBLIC)) {
            throw new ApplicationException(GroupErrorCode.THIS_OPERATE_NEED_MANAGER_ROLE);
        }



        Group group1 = new Group();
        BeanUtils.copyPropertiesIgnoreNull(req,group1);
        Group insert = groupRepository.save(group1);
        if(insert == null){
            throw new ApplicationException(GroupErrorCode.UPDATE_GROUP_BASE_INFO_ERROR);
        }

        return ResponseVo.successResponse();
    }

    @Override
    public ResponseVo getJoinedGroup(GetJoinedGroupReq req) {
        List<Integer> groupIds = groupMemberService.getMemberJoinedGroup(req).getData();
        List<Group> pageGroup = groupRepository.findAllById(groupIds);
        return ResponseVo.successResponse(pageGroup);
    }

    @Override
    public ResponseVo destroyGroup(Integer groupId,Integer appId,String userId) {
       Optional<Group> group = groupRepository.findById(groupId);
       if(!group.isPresent()){
           return ResponseVo.successResponse(GroupErrorCode.GROUP_IS_NOT_EXIST);
       }

       if(!group.get().getOwnerId().equals(userId)){
           return ResponseVo.errorResponse(GroupErrorCode.THIS_OPERATE_NEED_OWNER_ROLE);
       }
       if(group.get().getStatus().equals(GroupStatus.DESTROY)){
            return ResponseVo.errorResponse(GroupErrorCode.GROUP_IS_DESTROY);
       }
       groupRepository.updateStatusById(GroupStatus.DESTROY,groupId);
       return ResponseVo.successResponse();
    }

    @Override
    @Transactional
    public ResponseVo transferGroup(TransferGroupReq req) {
        ResponseVo<GetRoleInGroupResp> roleInGroupOne = groupMemberService.getRoleInGroupOne(req.getGroupId(), req.getOperator(), req.getAppId());
        if (!roleInGroupOne.isOk()) {
            return roleInGroupOne;
        }

        if (roleInGroupOne.getData().getRole() != GroupMemberRole.OWNER) {
            return ResponseVo.errorResponse(GroupErrorCode.THIS_OPERATE_NEED_OWNER_ROLE);
        }

        ResponseVo<GetRoleInGroupResp> newOwnerRole = groupMemberService.getRoleInGroupOne(req.getGroupId(), req.getOwnerId(), req.getAppId());
        if (!newOwnerRole.isOk()) {
            return newOwnerRole;
        }
        Optional<Group> group = groupRepository.findById(req.getGroupId());
        if(!group.isPresent()){
            return ResponseVo.errorResponse(GroupErrorCode.GROUP_IS_NOT_EXIST);
        }
        if(group.get().getStatus().equals(GroupStatus.DESTROY)){
            return ResponseVo.errorResponse(GroupErrorCode.GROUP_IS_DESTROY);
        }
        groupRepository.updateOwnerIdById(req.getOwnerId(),req.getGroupId());
        groupMemberService.transferGroupMember(req.getOwnerId(),req.getGroupId(),req.getAppId());
        return ResponseVo.successResponse();
    }

    @Override
    @Transactional
    public ResponseVo muteGroup(MuteGroupReq req) {

        ResponseVo<Group> groupResp = getGroup(req.getGroupId());
        if (!groupResp.isOk()) {
            return groupResp;
        }

        if(groupResp.getData().getStatus().equals(GroupStatus.DESTROY)){
            throw new ApplicationException(GroupErrorCode.GROUP_IS_DESTROY);
        }

        ResponseVo<GetRoleInGroupResp> role = groupMemberService.getRoleInGroupOne(req.getGroupId(), req.getOperator(), req.getAppId());

        if (!role.isOk()) {
            return role;
        }

        GetRoleInGroupResp data = role.getData();

        if (!data.getRole().equals(GroupMemberRole.MANAGER) || !data.getRole().equals(GroupMemberRole.OWNER)) {
            throw new ApplicationException(GroupErrorCode.THIS_OPERATE_NEED_MANAGER_ROLE);
        }

        groupRepository.updateMuteById(req.getMute(),req.getGroupId());
        return ResponseVo.successResponse();
    }

    @Override
    public Long getUserGroupMaxSeq(String userId, Integer appId) {
        return null;
    }
}
