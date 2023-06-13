package com.mogen.im.service.group.controller;

import com.mogen.im.common.ResponseVo;
import com.mogen.im.service.group.modle.req.*;
import com.mogen.im.service.group.service.GroupMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("v1/group/member")
public class GroupMemberController {



    @Autowired
    private GroupMemberService groupMemberService;



    @RequestMapping("/importGroupMember")
    public ResponseVo importGroupMember(@RequestBody @Validated ImportGroupMemberReq req, Integer appId)  {
        req.setAppId(appId);
        return groupMemberService.importGroupMember(req);
    }

    @RequestMapping("/add")
    public ResponseVo addMember(@RequestBody @Validated AddGroupMemberReq req, Integer appId, String identifier)  {
        req.setAppId(appId);
        req.setOperator(identifier);
        return groupMemberService.addMember(req);
    }

    @RequestMapping("/remove")
    public ResponseVo removeMember(@RequestBody @Validated RemoveGroupMemberReq req, Integer appId, String identifier)  {
        req.setAppId(appId);
        req.setOperator(identifier);
        return groupMemberService.removeMember(req);
    }

    @RequestMapping("/exit")
    public ResponseVo memberExit(@RequestBody @Validated ExitGroupReq req, Integer appId, String identifier)  {
        req.setAppId(appId);
        req.setOperator(identifier);
        return groupMemberService.exitGroup(req);
    }

    @RequestMapping("/update")
    public ResponseVo updateGroupMember(@RequestBody @Validated UpdateGroupMemberReq req, Integer appId, String identifier)  {
        req.setAppId(appId);
        req.setOperator(identifier);
        return groupMemberService.updateGroupMember(req);
    }

    @RequestMapping("/speak")
    public ResponseVo speak(@RequestBody @Validated SpeakMemberReq req, Integer appId, String identifier)  {
        req.setAppId(appId);
        req.setOperator(identifier);
        return groupMemberService.speak(req);
    }



}
