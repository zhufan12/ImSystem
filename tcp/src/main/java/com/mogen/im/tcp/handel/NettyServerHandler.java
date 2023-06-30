package com.mogen.im.tcp.handel;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mogen.im.codec.pack.ChatMessageAck;
import com.mogen.im.codec.pack.LoginPack;
import com.mogen.im.codec.proto.Message;
import com.mogen.im.codec.proto.MessagePack;
import com.mogen.im.common.ResponseVo;
import com.mogen.im.common.constants.Constants;
import com.mogen.im.common.enums.ConnectStatus;
import com.mogen.im.common.enums.GroupEventAction;
import com.mogen.im.common.enums.MessageAction;
import com.mogen.im.common.enums.action.SystemAction;
import com.mogen.im.common.messasge.req.CheckSendMessageReq;
import com.mogen.im.common.model.UserClientDto;
import com.mogen.im.common.model.UserSession;
import com.mogen.im.common.utils.JsonUtils;
import com.mogen.im.tcp.feign.FeignMessageService;
import com.mogen.im.tcp.publish.MessageProducer;
import com.mogen.im.tcp.redis.RedisManager;
import com.mogen.im.tcp.utils.SessionSocketHolder;
import feign.Feign;
import feign.Request;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import lombok.extern.java.Log;
import org.redisson.RedissonTopic;
import org.redisson.api.RBucket;
import org.redisson.api.RMap;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;

public class NettyServerHandler extends SimpleChannelInboundHandler<Message> {

    private static final Logger log = LoggerFactory.getLogger(NettyServerHandler.class);

    private Integer brokerId;

    private FeignMessageService feignMessageService;



    public NettyServerHandler(Integer brokerId,String logicUrl) {

        this.brokerId = brokerId;

        feignMessageService = Feign.builder()
                .encoder(new JacksonEncoder())
                .decoder(new JacksonDecoder())
                .options(new Request.Options(1000, 3500))
                .target(FeignMessageService.class, logicUrl);

    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {
        ObjectMapper mapper = JsonUtils.getInstance();
        int action = msg.getMessageHeader().getAction();
        String json = mapper.writeValueAsString(msg);
        JsonNode jsonNode = mapper.readTree(json);
        if (action == SystemAction.LOGIN.getAction()) {
            String userId = jsonNode.get("messagePack").get("userId").asText();
            UserSession userSession = new UserSession();
            userSession.setAppId(msg.getMessageHeader().getAppId());
            userSession.setClientType(msg.getMessageHeader().getClientType());
            userSession.setImei(msg.getMessageHeader().getImei());
            userSession.setVersion(msg.getMessageHeader().getVersion());
            userSession.setConnectState(ConnectStatus.ONLINE.ordinal());
            userSession.setUserId(userId);
            userSession.setBrokerId(brokerId);
            try {
                String hostAddress = InetAddress.getLocalHost().getHostAddress();
                userSession.setBrokerHost(hostAddress);
            }catch (Exception ex){
                ex.printStackTrace();
            }

            RedissonClient reredissonClient = RedisManager.getRedissonClient();

            RMap<String, String> redissonMap = reredissonClient.getMap(userSession.getAppId() + Constants.RedisConstants.UserSessionConstants + userSession.getUserId());
            redissonMap.put(userSession.getClientType() + ":" + userSession.getImei(), mapper.writeValueAsString(userSession));
            SessionSocketHolder.put(userSession.getAppId(), userId, userSession.getClientType(),userSession.getImei(),(NioSocketChannel) ctx.channel());

            UserClientDto userClientDto = new UserClientDto();
            userClientDto.setImei(userSession.getImei());
            userClientDto.setAppId(userSession.getAppId());
            userClientDto.setClientType(userSession.getClientType());
            userClientDto.setUserId(userSession.getUserId());

            RTopic topic = reredissonClient.getTopic(Constants.RedisConstants.UserLoginChannel);
            topic.publish(mapper.writeValueAsString(userClientDto));

            Channel channel = ctx.channel();
            channel.attr(AttributeKey.valueOf(Constants.UserId)).set(userId);
            channel.attr(AttributeKey.valueOf(Constants.AppId)).set(userSession.getAppId());
            channel.attr(AttributeKey.valueOf(Constants.ClientType)).set(userSession.getClientType());
            channel.attr(AttributeKey.valueOf(Constants.Imei)).set(userSession.getImei());

        } else if (action == SystemAction.LOGOUT.getAction()) {
            SessionSocketHolder.removeUserSession((NioSocketChannel) ctx.channel());
        }else if (action == SystemAction.PING.getAction()){
            Channel channel = ctx.channel();
            channel.attr(AttributeKey.valueOf(Constants.ReadTime)).set(System.currentTimeMillis());
        }else if (action == MessageAction.MSG_P2P.getAction()){

            CheckSendMessageReq checkSendMessageReq = new CheckSendMessageReq();
            checkSendMessageReq.setAppId(msg.getMessageHeader().getAppId());
            checkSendMessageReq.setAction(action);
            JsonNode msgPack = jsonNode.get("messagePack");
            String fromId = msgPack.get("fromId").asText();
            String toId = msgPack.get("toId").asText();
            checkSendMessageReq.setToId(toId);
            checkSendMessageReq.setFromId(fromId);
            ResponseVo responseVo = feignMessageService.checkSendMessage(checkSendMessageReq);
            if(responseVo.isOk()){
                MessageProducer.sendMessage(msg,action);
            }else {
                //ack
                Integer ackCommand = 0;
                if(ackCommand == MessageAction.MSG_P2P.getAction()){
                    ackCommand = MessageAction.MSG_ACK.getAction();
                }else {
                    ackCommand = GroupEventAction.GROUP_MSG_ACK.getAction();
                }
                ChatMessageAck chatMessageAck = new ChatMessageAck(msgPack.get("messageId").asText());
                MessagePack<ResponseVo> resp = new MessagePack<>();
                responseVo.setData(chatMessageAck);
                resp.setData(responseVo);
                resp.setAction(ackCommand);
                ctx.channel().writeAndFlush(resp);
            }
        }else {
            MessageProducer.sendMessage(msg,action);
        }
    }
}
