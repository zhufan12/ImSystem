package com.mogen.im.service.friendship.controller;

import com.mogen.im.common.ResponseVo;
import com.mogen.im.common.model.SyncReq;
import com.mogen.im.service.friendship.model.req.*;
import com.mogen.im.service.friendship.service.FriendShipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("v1/friendship")
public class FriendShipController {


    @Autowired
    private FriendShipService friendShipService;


    @RequestMapping("/importFriendShip")
    public ResponseVo importFriendShip(@RequestBody @Validated ImportFriendShipReq req, Integer appId){
        req.setAppId(appId);
        return friendShipService.importFriendShip(req);
    }


    @RequestMapping("/addFriend")
    public ResponseVo addFriend(@RequestBody @Validated AddFriendReq req, Integer appId){
        req.setAppId(appId);
        return friendShipService.addFiendShip(req);
    }

    @RequestMapping("/updateFriend")
    public ResponseVo updateFriend(@RequestBody @Validated UpdateFriendReq req, Integer appId){
        req.setAppId(appId);
        return friendShipService.updateFriend(req);
    }

    @RequestMapping("/deleteFriend")
    public ResponseVo deleteFriend(@RequestBody @Validated DeleteFriendReq req, Integer appId){
        req.setAppId(appId);
        return friendShipService.deleteFriend(req);
    }

    @RequestMapping("/deleteAllFriend")
    public ResponseVo deleteAllFriend(@RequestBody @Validated DeleteFriendReq req, Integer appId){
        req.setAppId(appId);
        return friendShipService.deleteAllFriend(req);
    }

    @RequestMapping("/getAllFriendShip")
    public ResponseVo getAllFriendShip(@RequestBody @Validated GetAllFriendShipReq req, Integer appId){
        req.setAppId(appId);
        return friendShipService.getAllFriendShip(req);
    }

    @RequestMapping("/getRelation")
    public ResponseVo getRelation(@RequestBody @Validated GetRelationReq req, Integer appId){
        req.setAppId(appId);
        return friendShipService.getRelation(req);
    }

    @RequestMapping("/checkFriend")
    public ResponseVo checkFriend(@RequestBody @Validated CheckFriendShipReq req, Integer appId){
        req.setAppId(appId);
        return friendShipService.checkFriendship(req);
    }


    @RequestMapping("/addBlack")
    public ResponseVo addBlack(@RequestBody @Validated FriendShipBlackReq req, Integer appId){
        req.setAppId(appId);
        return friendShipService.addBlack(req);
    }

    @RequestMapping("/deleteBlack")
    public ResponseVo deleteBlack(@RequestBody @Validated FriendShipBlackReq req, Integer appId){
        req.setAppId(appId);
        return friendShipService.deleteBlack(req);
    }

    @RequestMapping("/checkBlack")
    public ResponseVo checkBlack(@RequestBody @Validated CheckFriendShipReq req, Integer appId){
        req.setAppId(appId);
        return friendShipService.checkBlack(req);
    }

    @RequestMapping("/syncFriendshipList")
    public ResponseVo syncFriendshipList(@RequestBody @Validated
                                         SyncReq req, Integer appId){
        req.setAppId(appId);
        return friendShipService.syncFriendshipList(req);
    }




}
