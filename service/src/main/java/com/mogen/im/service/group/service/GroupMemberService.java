package com.mogen.im.service.group.service;

import com.mogen.im.common.ResponseVo;
import com.mogen.im.service.group.modle.req.*;
import com.mogen.im.service.group.modle.resp.GetRoleInGroupResp;

import java.util.List;

public interface GroupMemberService {

    public ResponseVo importGroupMember(ImportGroupMemberReq req);

    public  ResponseVo addGroupMember(Integer groupId, Integer appId, GroupMemberDto dto);

    public ResponseVo addMember(AddGroupMemberReq req);

    public ResponseVo removeMember(RemoveGroupMemberReq req);

    public ResponseVo removeGroupMember(Integer groupId, Integer appId, String memberId);

    public ResponseVo<List<Integer>> getMemberJoinedGroup(GetJoinedGroupReq req);

    public ResponseVo exitGroup(ExitGroupReq req);

    public ResponseVo<GetRoleInGroupResp> getRoleInGroupOne(Integer groupId, String userId, Integer appId);

    public ResponseVo transferGroupMember(String owner, Integer groupId, Integer appId);


    public ResponseVo updateGroupMember(UpdateGroupMemberReq req);


    public ResponseVo speak(SpeakMemberReq req);



}
