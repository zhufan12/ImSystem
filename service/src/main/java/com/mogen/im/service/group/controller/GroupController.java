package com.mogen.im.service.group.controller;

import com.mogen.im.common.ResponseVo;
import com.mogen.im.service.group.modle.req.*;
import com.mogen.im.service.group.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("v1/group")
public class GroupController {


    @Autowired
    private GroupService groupService;


    @RequestMapping("/importGroup")
    public ResponseVo importGroup(@RequestBody @Validated ImportGroupReq req, Integer appId, String identifier)  {
        req.setAppId(appId);
        req.setOperator(identifier);
        return groupService.importGroup(req);
    }

    @RequestMapping("/createGroup")
    public ResponseVo createGroup(@RequestBody @Validated CreateGroupReq req, Integer appId, String identifier)  {
        req.setAppId(appId);
        req.setOperator(identifier);
        return groupService.createGroup(req);
    }

    @RequestMapping("/getGroupInfo/{groupId}")
    public ResponseVo getGroupInfo(@PathVariable("groupId") Integer groupId, Integer appId)  {
        return groupService.getGroup(groupId);
    }

    @RequestMapping("/update")
    public ResponseVo update(@RequestBody @Validated UpdateGroupReq req, Integer appId, String identifier)  {
        req.setAppId(appId);
        req.setOperator(identifier);
        return groupService.updateBaseGroupInfo(req);
    }


    @RequestMapping("/getJoinedGroup")
    public ResponseVo getJoinedGroup(@RequestBody @Validated GetJoinedGroupReq req, Integer appId, String identifier)  {
        req.setAppId(appId);
        req.setOperator(identifier);
        return groupService.getJoinedGroup(req);
    }

    @RequestMapping("/destroyGroup/{groupId}")
    public ResponseVo destroyGroup(@PathVariable("groupId")Integer groupId, Integer appId, String identifier)  {
        return groupService.destroyGroup(groupId, appId,identifier);
    }

    @RequestMapping("/transferGroup")
    public ResponseVo transferGroup(@RequestBody @Validated TransferGroupReq req, Integer appId, String identifier)  {
        req.setAppId(appId);
        req.setOperator(identifier);
        return groupService.transferGroup(req);
    }


    @RequestMapping("/forbidSendMessage")
    public ResponseVo forbidSendMessage(@RequestBody @Validated MuteGroupReq req, Integer appId, String identifier)  {
        req.setAppId(appId);
        req.setOperator(identifier);
        return groupService.muteGroup (req);
    }


}
