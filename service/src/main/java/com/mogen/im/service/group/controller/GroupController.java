package com.mogen.im.service.group.controller;

import com.mogen.im.common.ResponseVo;
import com.mogen.im.common.model.SyncReq;
import com.mogen.im.service.group.modle.req.*;
import com.mogen.im.service.group.service.GroupMessageServer;
import com.mogen.im.service.group.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("v1/group")
public class GroupController {


    @Autowired
    private GroupService groupService;


    @Autowired
    private GroupMessageServer groupMessageServer;


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
    public ResponseVo destroyGroup(@RequestBody @Validated DestroyGroupReq destroyGroupReq,String identifier)  {
        destroyGroupReq.setOperator(identifier);
        return groupService.destroyGroup(destroyGroupReq);
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


    @RequestMapping("/sendMessage")
    public ResponseVo sendMessage(@RequestBody @Validated SendGroupMessageReq
                                          req, Integer appId,
                                  String identifier)  {
        req.setAppId(appId);
        req.setOperator(identifier);
        return ResponseVo.successResponse(groupMessageServer.send(req));
    }

    @RequestMapping("/syncJoinedGroup")
    public ResponseVo syncJoinedGroup(@RequestBody @Validated SyncReq req, Integer appId, String identifier)  {
        req.setAppId(appId);
        return groupService.syncJoinedGroupList(req);
    }


}
