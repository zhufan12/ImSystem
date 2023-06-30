package com.mogen.im.service.user.controller;


import com.mogen.im.common.ResponseVo;
import com.mogen.im.common.enums.ClientType;
import com.mogen.im.common.route.RouteHandle;
import com.mogen.im.common.route.RouteInfo;
import com.mogen.im.common.utils.RouteInfoParseUtil;
import com.mogen.im.service.user.model.req.*;
import com.mogen.im.service.user.service.UserService;
import com.mogen.im.service.utils.ZKit;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("v1/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private RouteHandle routeHandle;

    @Autowired
    private ZKit zKit;


    @PostMapping("/import")
    public ResponseVo importUsers(@RequestBody @Validated ImportUserReq importUserReq,
                                 @RequestParam @NotEmpty(message = "appId can't be null") Integer appId){
        importUserReq.setAppId(appId);
        return  userService.importUsers(importUserReq);
    }

    @RequestMapping("/deleteUser")
    public ResponseVo deleteUser(@RequestBody @Validated DeleteUserReq req, Integer appId) {
        req.setAppId(appId);
        return userService.deleteUser(req);
    }

    @RequestMapping("/login")
    public ResponseVo login(@RequestBody @Validated LoginReq req, Integer appId) throws Exception {
        req.setAppId(appId);

        ResponseVo loginResp = userService.login(req);
        if(loginResp.isOk()){
            List<String> allNodes = null;
            if(req.getClientType() == ClientType.WEBAPI.ordinal()){
                allNodes = zKit.getAllWebNode();
            }else {
                allNodes = zKit.getAllTcpNode();
            }

            String serviceNode = routeHandle.routeServer(allNodes,req.getUserPid());

            RouteInfo routeInfo = RouteInfoParseUtil.parse(serviceNode);

            return ResponseVo.successResponse(routeInfo);
        }
        return ResponseVo.successResponse("");
    }

    @RequestMapping("/getUserSequence")
    public ResponseVo getUserSequence(@RequestBody @Validated
                                      GetUserSequenceReq req, Integer appId) {
        req.setAppId(appId);
        userService.getUserSequence(req);
        return ResponseVo.builder().build();
    }

    @RequestMapping("/getUserInfo")
    public ResponseVo getUserInfo(@RequestBody  @Validated GetUserInfoReq req, Integer appId){
        req.setAppId(appId);
        return userService.getUserInfo(req);
    }

    @RequestMapping("/getSingleUserInfo")
    public ResponseVo getSingleUserInfo(@RequestBody @Validated UserPid req, Integer appId) {
        req.setAppId(appId);
        return  userService.getSingleUserInfo(req.getUserPid(),req.getAppId());
    }

    @RequestMapping("/modifyUserInfo")
    public ResponseVo modifyUserInfo(@RequestBody @Validated ModifyUserInfoReq req, Integer appId) {
        req.setAppId(appId);
        return userService.modifyUserInfo(req);
    }


}



