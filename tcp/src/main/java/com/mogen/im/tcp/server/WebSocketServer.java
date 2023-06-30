package com.mogen.im.tcp.server;

import com.mogen.im.codec.MessageDecoder;
import com.mogen.im.codec.WebSocketMessageDecoder;
import com.mogen.im.codec.WebSocketMessageEncoder;
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
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebSocketServer {


    private static final Logger log = LoggerFactory.getLogger(WebSocketServer.class);

    private  BootstrapConfig.TcpConfig  config;
    private EventLoopGroup mainGroup;
    private EventLoopGroup subGroup;
    private ServerBootstrap server;
    public WebSocketServer(BootstrapConfig.TcpConfig config) {
        this.config = config;
        mainGroup = new NioEventLoopGroup();
        subGroup = new NioEventLoopGroup();
        server = new ServerBootstrap();
        server.channel(NioServerSocketChannel.class)
                .group(mainGroup,subGroup)
                .option(ChannelOption.SO_BACKLOG,10240)
                .option(ChannelOption.SO_REUSEADDR,true)
                .childOption(ChannelOption.TCP_NODELAY,true)
                .childOption(ChannelOption.SO_KEEPALIVE,true)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast("http-codec",new HttpServerCodec());
                        pipeline.addLast("http-chunked",new ChunkedWriteHandler());
                        pipeline.addLast("aggregator",new HttpObjectAggregator(65535));
                        pipeline.addLast(new WebSocketServerProtocolHandler("/ws"));
                        pipeline.addLast(new WebSocketMessageDecoder());
                        pipeline.addLast(new WebSocketMessageEncoder());
                        pipeline.addLast(new NettyServerHandler(config.getBrokerId(),config.getLogicUrl()));
                    }
                });

    }


    public void start(){
        server.bind(this.config.getWebSocketPort());
    }
}
