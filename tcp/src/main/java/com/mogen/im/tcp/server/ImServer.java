package com.mogen.im.tcp.server;

import com.mogen.im.codec.MessageDecoder;
import com.mogen.im.codec.config.BootstrapConfig;
import com.mogen.im.tcp.handel.HeartBeatHandler;
import com.mogen.im.tcp.handel.NettyServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImServer {

    private final static Logger logger = LoggerFactory.getLogger(ImServer.class);

    private  BootstrapConfig.TcpConfig  config;

    private EventLoopGroup mainGroup;
    private EventLoopGroup subGroup;

    private ServerBootstrap serverBootstrap;

    public ImServer(BootstrapConfig.TcpConfig config){
        this.config = config;

         mainGroup = new NioEventLoopGroup(config.getBoosThreadSize());
         subGroup = new NioEventLoopGroup(config.getBoosThreadSize());

         serverBootstrap = new ServerBootstrap();

         serverBootstrap.group(mainGroup,subGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG,10240) // service can accept max queue number
                .option(ChannelOption.SO_REUSEADDR,true) // the server port can duplicate
                .childOption(ChannelOption.TCP_NODELAY,true) // disable the TCP_NODELAY
                .childOption(ChannelOption.SO_KEEPALIVE,true)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        ChannelPipeline pipeline = socketChannel.pipeline();
                        pipeline.addLast(new MessageDecoder());
                        pipeline.addLast(new IdleStateHandler(0,0,10));
                        pipeline.addLast(new HeartBeatHandler(config.getHeartBeatTime()));
                        pipeline.addLast(new NettyServerHandler(config.getBrokerId(),config.getLogicUrl()));
                    }
                });
    }

    public void start(){
        this.serverBootstrap.bind(config.getTcpPort());
    }

}
