package com.mogen.im.service.user.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mogen.im.codec.pack.user.UserModifyPack;
import com.mogen.im.common.ResponseVo;
import com.mogen.im.common.config.AppConfig;
import com.mogen.im.common.constants.Constants;
import com.mogen.im.common.enums.action.SystemAction;
import com.mogen.im.common.enums.action.UserEventAction;
import com.mogen.im.common.utils.BeanUtils;
import com.mogen.im.service.friendship.model.resp.ImportFriendShipResp;
import com.mogen.im.service.group.entity.Group;
import com.mogen.im.service.group.service.GroupService;
import com.mogen.im.service.user.entity.User;
import com.mogen.im.service.user.model.req.*;
import com.mogen.im.service.user.model.resp.GetUserInfoResp;
import com.mogen.im.service.user.repository.UserRepository;
import com.mogen.im.service.user.service.UserService;
import com.mogen.im.service.utils.MessageProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
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

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MessageProducer messageProducer;


    @Autowired
    private GroupService groupService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

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
        UserModifyPack userModifyPack = new UserModifyPack();
        BeanUtils.copyPropertiesIgnoreNull(req,userModifyPack);
        messageProducer.sendToUser(req.getUserPid(),req.getClientType(),req.getImei(),req.getAppId()
                ,UserEventAction.USER_MODIFY,userModifyPack);
        return ResponseVo.successResponse();
    }

    @Override
    public ResponseVo login(LoginReq req) {
        return  ResponseVo.successResponse();
    }

    @Override
    public ResponseVo getUserSequence(GetUserSequenceReq req) {
        Map<Object, Object> map = stringRedisTemplate.opsForHash().entries(req.getAppId() + ":" + Constants.RedisConstants.SeqPrefix + ":" + req.getOperator());
        Long groupSeq = groupService.getUserGroupMaxSeq(req.getOperator(),req.getAppId());
        map.put(Constants.SeqConstants.Group,groupSeq);
        return ResponseVo.successResponse(map);
    }
}
