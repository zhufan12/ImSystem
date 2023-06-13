package com.mogen.im.service.user.service.impl;

import com.mogen.im.common.ResponseVo;
import com.mogen.im.common.utils.BeanUtils;
import com.mogen.im.service.friendship.model.resp.ImportFriendShipResp;
import com.mogen.im.service.user.entity.User;
import com.mogen.im.service.user.model.req.*;
import com.mogen.im.service.user.model.resp.GetUserInfoResp;
import com.mogen.im.service.user.repository.UserRepository;
import com.mogen.im.service.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.mogen.im.common.enums.UserErrorCode.MODIFY_USER_ERROR;
import static com.mogen.im.common.enums.UserErrorCode.USER_IS_NOT_EXIST;

@Service
public class UserServiceImpl implements UserService {

    public static final int MAX_IMPORT_USER = 100;

    @Autowired
    private UserRepository userRepository;

    @Override
    public ResponseVo importUsers(ImportUserReq importUserReq) {
        List<String> successUser = new ArrayList<>();
        List<String> failedUser = new ArrayList<>();
        ImportFriendShipResp importFriendShipResp = new ImportFriendShipResp();
        importUserReq.getUserdata().stream().forEach(user -> {
            user.setAppId(importUserReq.getAppId());
            User saveUser = null;
            try {
                saveUser = userRepository.saveAndFlush(user);
            }catch (Exception exception){

            }
            if(saveUser != null) {
                successUser.add(saveUser.getUserPid());
            }else {
                failedUser.add(user.getUserPid());
            }
        });
        importFriendShipResp.setSuccessId(successUser);
        importFriendShipResp.setErrorId(failedUser);
        return ResponseVo.successResponse(importFriendShipResp);
    }

    @Override
    public ResponseVo getUserInfo(GetUserInfoReq req) {
        List<User> users = userRepository.findByUserPidInAndAppId(req.getUserPids(),req.getAppId());
        List<String> notFoundUser = new ArrayList<>();
        GetUserInfoResp resp = new GetUserInfoResp();
        resp.setUserDataItem(users);
        if(users.isEmpty()){
            resp.setFailUser(req.getUserPids());
            return ResponseVo.errorResponse(USER_IS_NOT_EXIST);
        }
        Map<String,User> userMap = users.stream().collect(Collectors.toMap(o-> o.getUserPid(), Function.identity()));
        req.getUserPids().forEach(userPid -> {
            if(!userMap.containsKey(userPid)){
                notFoundUser.add(userPid);
            }
        });

        resp.setFailUser(notFoundUser);
        return ResponseVo.successResponse(resp);
    }

    @Override
    public ResponseVo getSingleUserInfo(String userId, Integer appId) {
        Optional<User> user = userRepository.findByUserPidAndAppId(userId,appId);
        if(!user.isPresent()){
            return ResponseVo.errorResponse(USER_IS_NOT_EXIST);
        }
        return ResponseVo.successResponse(user.get());
    }

    @Override
    @Transactional
    public ResponseVo deleteUser(DeleteUserReq req) {
        List<String> successUser = new ArrayList<>();
        List<String> failedUser = new ArrayList<>();
        req.getUserPids().stream().forEach(item -> {
            int deleted = userRepository.deleteByUserPidAndAppId(item,req.getAppId());
            if(deleted == 1){
                successUser.add(item);
            }else {
                failedUser.add(item);
            }
        });
        ImportFriendShipResp resp = new ImportFriendShipResp();
        resp.setErrorId(failedUser);
        resp.setSuccessId(successUser);
        return ResponseVo.successResponse(resp);
    }

    @Override
    public ResponseVo modifyUserInfo(ModifyUserInfoReq req) {
        Optional<User> user = userRepository.findByUserPidAndAppId(req.getUserPid(),req.getAppId());
        if(!user.isPresent()){
            return ResponseVo.errorResponse(USER_IS_NOT_EXIST);
        }
        User user1 = new User();
        BeanUtils.copyPropertiesIgnoreNull(user.get(),user1);
        BeanUtils.copyPropertiesIgnoreNull(req,user1);
        user1.setId(user.get().getId());
        User updateUser =  userRepository.save(user1);
        if(updateUser == null){
            return ResponseVo.errorResponse(MODIFY_USER_ERROR);
        }
        return ResponseVo.successResponse();
    }

    @Override
    public ResponseVo login(LoginReq req) {
        return  null;
    }

    @Override
    public ResponseVo getUserSequence(GetUserSequenceReq req) {
        return null;
    }
}
