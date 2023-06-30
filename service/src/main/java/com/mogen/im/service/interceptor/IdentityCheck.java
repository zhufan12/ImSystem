package com.mogen.im.service.interceptor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mogen.im.common.ResponseCode;
import com.mogen.im.common.ResponseVo;
import com.mogen.im.common.config.AppConfig;
import com.mogen.im.common.constants.Constants;
import com.mogen.im.common.enums.GateWayErrorCode;
import com.mogen.im.common.enums.UserTypeEnum;
import com.mogen.im.common.exception.ApplicationExceptionEnum;
import com.mogen.im.common.utils.SigAPI;
import com.mogen.im.service.user.entity.User;
import com.mogen.im.service.user.service.UserService;
import io.micrometer.common.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class IdentityCheck {
    private static Logger logger = LoggerFactory.getLogger(IdentityCheck.class);

    @Autowired
    UserService userService;

    @Autowired
    AppConfig appConfig;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    public ApplicationExceptionEnum checkUserSig(String identifier,
                                                 String appId, String userSig){

        String cacheUserSig = stringRedisTemplate.opsForValue()
                .get(appId + ":" + Constants.RedisConstants.userSign + ":"
                        + identifier + userSig);
        if(!StringUtils.isBlank(cacheUserSig) && Long.valueOf(cacheUserSig)
                >  System.currentTimeMillis() / 1000){
            this.setIsAdmin(identifier,Integer.valueOf(appId));
            return ResponseCode.SUCCESSD;
        }


        String privateKey = appConfig.getPrivateKey();


        SigAPI sigAPI = new SigAPI(Long.valueOf(appId), privateKey);


        JsonNode jsonObject = sigAPI.decodeUserSig(userSig);

        Long expireTime = 0L;
        Long expireSec = 0L;
        Long time = 0L;
        String decoerAppId = "";
        String decoderidentifier = "";

        try {
            decoerAppId = jsonObject.get("TLS.appId").asText();
            decoderidentifier = jsonObject.get("TLS.identifier").asText();
            String expireStr = jsonObject.get("TLS.expire").asText();
            String expireTimeStr = jsonObject.get("TLS.expireTime").asText();
            time = Long.valueOf(expireTimeStr);
            expireSec = Long.valueOf(expireStr);
            expireTime = Long.valueOf(expireTimeStr) + expireSec;
        }catch (Exception e){
            e.printStackTrace();
            logger.error("checkUserSig-error:{}",e.getMessage());
        }

        if(!decoderidentifier.equals(identifier)){
            return GateWayErrorCode.USERSIGN_OPERATE_NOT_MATE;
        }

        if(!decoerAppId.equals(appId)){
            return GateWayErrorCode.USERSIGN_IS_ERROR;
        }

        if(expireSec == 0L){
            return GateWayErrorCode.USERSIGN_IS_EXPIRED;
        }

        if(expireTime < System.currentTimeMillis() / 1000){
            return GateWayErrorCode.USERSIGN_IS_EXPIRED;
        }

        //appid + "xxx" + userId + sign
        String genSig = sigAPI.genUserSig(identifier, expireSec,time,null);
        if (genSig.toLowerCase().equals(userSig.toLowerCase()))
        {
            String key = appId + ":" + Constants.RedisConstants.userSign + ":"
                    +identifier + userSig;

            Long etime = expireTime - System.currentTimeMillis() / 1000;
            stringRedisTemplate.opsForValue().set(
                    key,expireTime.toString(),etime, TimeUnit.SECONDS
            );
            this.setIsAdmin(identifier,Integer.valueOf(appId));
            return ResponseCode.SUCCESSD;
        }

        return GateWayErrorCode.USERSIGN_IS_ERROR;
    }

    public void setIsAdmin(String identifier, Integer appId) {
        ResponseVo<User> singleUserInfo = userService.getSingleUserInfo(identifier, appId);
        if(singleUserInfo.isOk()){
            RequestHolder.set(singleUserInfo.getData().getUserType() == UserTypeEnum.APP_ADMIN.getCode());
        }else{
            RequestHolder.set(false);
        }
    }
}
