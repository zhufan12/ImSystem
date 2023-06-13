package com.mogen.im.service.group.service;

import com.mogen.im.common.ResponseVo;
import com.mogen.im.service.group.modle.req.*;

public interface GroupService {


    public ResponseVo importGroup(ImportGroupReq req);


    public ResponseVo getGroup(Integer groupId);


    public ResponseVo createGroup(CreateGroupReq req);

    public ResponseVo updateBaseGroupInfo(UpdateGroupReq req);


    public ResponseVo getJoinedGroup(GetJoinedGroupReq req);

    public ResponseVo destroyGroup(Integer groupId,Integer appId,String identifier);

    public ResponseVo transferGroup(TransferGroupReq req);

    public ResponseVo muteGroup(MuteGroupReq req);

    Long getUserGroupMaxSeq(String userId, Integer appId);
}
