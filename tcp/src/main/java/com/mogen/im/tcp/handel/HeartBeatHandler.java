package com.mogen.im.tcp.handel;

import com.mogen.im.common.constants.Constants;
import com.mogen.im.tcp.utils.SessionSocketHolder;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HeartBeatHandler extends ChannelInboundHandlerAdapter {

    private Long heartBeatTime;


    public HeartBeatHandler(Long heartBeatTime){
        this.heartBeatTime = heartBeatTime;
    }

    private static final Logger log = LoggerFactory.getLogger(HeartBeatHandler.class);

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if(evt instanceof IdleStateEvent){
            IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
            if(idleStateEvent.state() == IdleState.READER_IDLE){

            }else if(idleStateEvent.state() == IdleState.WRITER_IDLE){

            }else if (idleStateEvent.state() == IdleState.ALL_IDLE){
             Long lastReadTime = (Long)ctx.channel().attr(AttributeKey.valueOf(Constants.ReadTime)).get();
             long now = System.currentTimeMillis();
             if(lastReadTime != null && (lastReadTime - now) > heartBeatTime){
                 SessionSocketHolder.offlineUserSession((NioSocketChannel) ctx.channel());
             }

            }
         }
    }
}
