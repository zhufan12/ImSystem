package com.mogen.im.tcp.receiver;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mogen.im.codec.proto.MessagePack;
import com.mogen.im.common.constants.Constants;
import com.mogen.im.common.enums.ClientType;
import com.mogen.im.common.enums.DeviceMultiLoginModel;
import com.mogen.im.common.enums.action.SystemAction;
import com.mogen.im.common.model.UserClientDto;
import com.mogen.im.common.utils.JsonUtils;
import com.mogen.im.tcp.redis.RedisManager;
import com.mogen.im.tcp.utils.SessionSocketHolder;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.redisson.api.listener.MessageListener;
import org.redisson.client.RedisClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.spi.LoginModule;
import java.util.List;

public class UserLoginMessageListener {

    /*
    *  1单端登录：一端在线：踢掉除了本clinetType + imel 的设备
    *  2双端登录：允许pc/mobile 其中一端登录 + web端 踢掉除了本clinetType + imel 以外的web端设备
    *  3 三端登录：允许手机+pc+web，踢掉同端的其他imei 除了web
    *  4 不做任何处理
    *
    * */

    private static final Logger logger = LoggerFactory.getLogger(UserLoginMessageListener.class);



    private Integer loginModel;

    public UserLoginMessageListener(Integer loginModel) {
        this.loginModel = loginModel;
    }

    public void listenerUserLogin(){
        RedissonClient reredissonClient = RedisManager.getRedissonClient();
        RTopic topic = reredissonClient.getTopic(Constants.RedisConstants.UserLoginChannel);
        ObjectMapper mapper = JsonUtils.getInstance();
        topic.addListener(String.class, new MessageListener<String>() {
            @Override
            public void onMessage(CharSequence charSequence, String s) {
                logger.info("get user login topic message : {}",s);
                try {
                    UserClientDto userClientDto = mapper.readValue(s,UserClientDto.class);
                    List<NioSocketChannel> channels = SessionSocketHolder.get(userClientDto.getAppId(),userClientDto.getUserId());
                    for (NioSocketChannel nioSocketChannel : channels) {
                        if(loginModel == DeviceMultiLoginModel.ONE.getLoginMode()){
                            Integer clientType = (Integer) nioSocketChannel.attr(AttributeKey.valueOf(Constants.ClientType)).get();
                            String imei = (String) nioSocketChannel.attr(AttributeKey.valueOf(Constants.Imei)).get();

                            if(!(clientType + ":" + imei).equals(userClientDto.getClientType() + ":" + userClientDto.getImei())){
                                MessagePack<Object> pack = new MessagePack<>();
                                pack.setToId((String) nioSocketChannel.attr(AttributeKey.valueOf(Constants.UserId)).get());
                                pack.setUserId((String) nioSocketChannel.attr(AttributeKey.valueOf(Constants.UserId)).get());
                                pack.setAction(SystemAction.MUTUALLOGIN.getAction());
                                nioSocketChannel.writeAndFlush(pack);
                            }

                        }else if(loginModel == DeviceMultiLoginModel.TWO.getLoginMode()){
                            if(userClientDto.getClientType() == ClientType.WEB.ordinal()){
                                continue;
                            }
                            Integer clientType = (Integer) nioSocketChannel.attr(AttributeKey.valueOf(Constants.ClientType)).get();

                            if (clientType == ClientType.WEB.ordinal()){
                                continue;
                            }
                            String imei = (String) nioSocketChannel.attr(AttributeKey.valueOf(Constants.Imei)).get();
                            if(!(clientType + ":" + imei).equals(userClientDto.getClientType() + ":" + userClientDto.getImei())){
                                MessagePack<Object> pack = new MessagePack<>();
                                pack.setToId((String) nioSocketChannel.attr(AttributeKey.valueOf(Constants.UserId)).get());
                                pack.setUserId((String) nioSocketChannel.attr(AttributeKey.valueOf(Constants.UserId)).get());
                                pack.setAction(SystemAction.MUTUALLOGIN.getAction());
                                nioSocketChannel.writeAndFlush(pack);
                            }

                        }else if(loginModel == DeviceMultiLoginModel.THREE.getLoginMode()){

                            Integer clientType = (Integer) nioSocketChannel.attr(AttributeKey.valueOf(Constants.ClientType)).get();
                            String imei = (String) nioSocketChannel.attr(AttributeKey.valueOf(Constants.Imei)).get();
                            if(userClientDto.getClientType() == ClientType.WEB.ordinal()){
                                continue;
                            }

                            Boolean isSameClient = false;
                            if((clientType == ClientType.IOS.ordinal() ||
                                    clientType == ClientType.ANDROID.ordinal()) &&
                                    (userClientDto.getClientType() == ClientType.IOS.ordinal() ||
                                            userClientDto.getClientType() == ClientType.ANDROID.ordinal())){
                                isSameClient = true;
                            }

                            if((clientType == ClientType.MAC.ordinal() ||
                                    clientType == ClientType.WINDOWS.ordinal()) &&
                                    (userClientDto.getClientType() == ClientType.MAC.ordinal() ||
                                            userClientDto.getClientType() == ClientType.WINDOWS.ordinal())){
                                isSameClient = true;
                            }

                            if(isSameClient && !(clientType + ":" + imei).equals(userClientDto.getClientType()+":"+userClientDto.getImei())){
                                MessagePack<Object> pack = new MessagePack<>();
                                pack.setToId((String) nioSocketChannel.attr(AttributeKey.valueOf(Constants.UserId)).get());
                                pack.setUserId((String) nioSocketChannel.attr(AttributeKey.valueOf(Constants.UserId)).get());
                                pack.setAction(SystemAction.MUTUALLOGIN.getAction());
                                nioSocketChannel.writeAndFlush(pack);
                            }
                        }
                    }
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

}
