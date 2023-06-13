package com.mogen.im.service.user.controller;


import com.mogen.im.common.ResponseVo;
import com.mogen.im.service.user.model.req.*;
import com.mogen.im.service.user.service.UserService;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("v1/user")
public class UserController {

    @Autowired
    private UserService userService;

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
    public ResponseVo login(@RequestBody @Validated LoginReq req, Integer appId) {
        req.setAppId(appId);
        return ResponseVo.builder().build();
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



