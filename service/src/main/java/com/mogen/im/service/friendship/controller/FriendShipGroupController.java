package com.mogen.im.service.friendship.controller;

import com.mogen.im.common.ResponseVo;
import com.mogen.im.service.friendship.model.req.AddFriendShipGroupReq;
import com.mogen.im.service.friendship.model.req.DeleteFriendShipGroupReq;
import com.mogen.im.service.friendship.model.req.FriendShipGroupMemberReq;
import com.mogen.im.service.friendship.service.FriendShipGroupMemberService;
import com.mogen.im.service.friendship.service.FriendShipGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("v1/friendship/group")
public class FriendShipGroupController {


    @Autowired
    private FriendShipGroupMemberService friendShipGroupMemberService;

    @Autowired
    private FriendShipGroupService friendShipGroupService;



    @RequestMapping("/add")
    public ResponseVo add(@RequestBody @Validated AddFriendShipGroupReq req, Integer appId)  {
        req.setAppId(appId);
        return friendShipGroupService.addGroup(req);
    }

    @RequestMapping("/del")
    public ResponseVo del(@RequestBody @Validated DeleteFriendShipGroupReq req, Integer appId)  {
        req.setAppId(appId);
        return friendShipGroupService.deleteGroup(req);
    }

    @RequestMapping("/member/add")
    public ResponseVo memberAdd(@RequestBody @Validated FriendShipGroupMemberReq req, Integer appId)  {
        req.setAppId(appId);
        return friendShipGroupMemberService.addGroupMember(req);
    }

    @RequestMapping("/member/del")
    public ResponseVo memberDel(@RequestBody @Validated FriendShipGroupMemberReq req, Integer appId)  {
        req.setAppId(appId);
        return friendShipGroupMemberService.delGroupMember(req);
    }
}
