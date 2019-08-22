package com.dewfn.netty.rpc.netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class RpcServer {


    public static void run() throws Exception{
        EventLoopGroup bossGroup=new NioEventLoopGroup(1);
        EventLoopGroup workerGroup=new NioEventLoopGroup(2);
        EventLoopGroup workerGroup2=new NioEventLoopGroup(4);
        try {

            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup,workerGroup).channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        public void initChannel(SocketChannel ch) throws Exception {

                            ChannelPipeline pipeline = ch.pipeline();

                            pipeline.addLast(new DelimiterBasedFrameDecoder(2046, Delimiters.lineDelimiter()));
                            pipeline.addLast(new StringDecoder(CharsetUtil.UTF_8));
                            pipeline.addLast(new StringEncoder(CharsetUtil.UTF_8));
                            pipeline.addLast(new ServerReceiveHandler());
                            pipeline.addLast(workerGroup2,new ServerLocalCallHandler());
                        };
                    });
            log.info("开户RPC监听端口:9999");
            ChannelFuture channelFuture = serverBootstrap.bind(9999).sync();

            channelFuture.channel().closeFuture().sync();


        }finally{
            //资源优雅释放
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
