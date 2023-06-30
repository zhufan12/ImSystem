package com.mogen.im.service.group.service;

import com.mogen.im.common.ResponseVo;
import com.mogen.im.service.group.entity.GroupMember;
import com.mogen.im.service.group.modle.req.*;
import com.mogen.im.service.group.modle.resp.GetRoleInGroupResp;

import java.util.Collection;
import java.util.List;

public interface GroupMemberService {

    public ResponseVo importGroupMember(ImportGroupMemberReq req);

    public  ResponseVo addGroupMember(Integer groupId, Integer appId, GroupMemberDto dto);

    public ResponseVo addMember(AddGroupMemberReq req);

    public ResponseVo removeMember(RemoveGroupMemberReq req);

    public ResponseVo removeGroupMember(Integer groupId, Integer appId, String memberId);

    public ResponseVo<List<Integer>> getMemberJoinedGroup(GetJoinedGroupReq req);

    public ResponseVo exitGroup(ExitGroupReq req);

    public List<String> getGroupMemberId(Integer groupId);

    public ResponseVo<GetRoleInGroupResp> getRoleInGroupOne(Integer groupId, String userId, Integer appId);

    public ResponseVo transferGroupMember(String owner, Integer groupId, Integer appId);


    public ResponseVo updateGroupMember(UpdateGroupMemberReq req);


    public ResponseVo speak(SpeakMemberReq req);


     public List<GroupMember> getGroupManager(Integer groupId);


    public ResponseVo<List<Integer>> syncMemberJoinedGroup(String memberId, Integer appId);


}
