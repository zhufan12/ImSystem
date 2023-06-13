package com.mogen.im.service.friendship.controller;

import com.mogen.im.common.ResponseVo;
import com.mogen.im.service.friendship.model.req.ApprovedFriendRequestReq;
import com.mogen.im.service.friendship.model.req.GetFriendShipRequestReq;
import com.mogen.im.service.friendship.service.FriendShipRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("v1/friendshipRequest")
public class FriendShipRequestController {


    @Autowired
    private FriendShipRequestService friendShipRequestService;


    @RequestMapping("/approveFriendRequest")
    public ResponseVo approveFriendRequest(@RequestBody @Validated
                                           ApprovedFriendRequestReq req, Integer appId){
        req.setAppId(appId);
        return friendShipRequestService.approvedFriendRequest(req);
    }
    @RequestMapping("/getFriendRequest")
    public ResponseVo getFriendRequest(@RequestBody @Validated GetFriendShipRequestReq req, Integer appId){
        req.setAppId(appId);
        return friendShipRequestService.getFriendRequest(req.getFromId(),req.getAppId());
    }

    @RequestMapping("/readFriendShipRequestReq")
    public ResponseVo readFriendShipRequestReq(@RequestBody @Validated GetFriendShipRequestReq req, Integer appId){
        req.setAppId(appId);
        return friendShipRequestService.readFriendShipRequestReq(req);
    }
}
