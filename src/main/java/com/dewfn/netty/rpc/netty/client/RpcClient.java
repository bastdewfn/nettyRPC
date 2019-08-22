package com.dewfn.netty.rpc.netty.client;

import com.alibaba.fastjson.JSON;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;
import lombok.extern.log4j.Log4j2;
import com.dewfn.netty.rpc.MyRequestEntity;
import com.dewfn.netty.rpc.netty.NettyResponse;

@Log4j2
public class RpcClient {

    public RpcClient(MyRequestEntity requestEntity) {
        this.requestEntity = requestEntity;
    }

    private MyRequestEntity requestEntity;
    private Channel channel;

    EventLoopGroup eventLoopGroup;
    private long timeout = 10000;

    public NettyResponse rpcCall() throws InterruptedException {
        NettyResponse responseEntity = new NettyResponse(timeout);


        eventLoopGroup = new NioEventLoopGroup(1);
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(eventLoopGroup).channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        public void initChannel(SocketChannel ch) throws Exception {
                            //ChannelPipeline用于存放管理ChannelHandel
                            //ChannelHandler用于处理请求响应的业务逻辑相关代码
                            ChannelPipeline pipeline = ch.pipeline();

                            pipeline.addLast(new DelimiterBasedFrameDecoder(4096, Delimiters.lineDelimiter()));
                            pipeline.addLast(new StringDecoder(CharsetUtil.UTF_8));
                            pipeline.addLast(new StringEncoder(CharsetUtil.UTF_8));
                            pipeline.addLast(new ClinetReceiveHandler((response) -> {
                                responseEntity.setResponseEntity(response);
                            }));
                        }

                        ;
                    });


            log.debug("连接打开 ");
            ChannelFuture channelFuture = bootstrap.connect(requestEntity.getHost(), requestEntity.getProt());
           // responseEntity.lock();
            channel=channelFuture.sync().channel();
            log.info("传送数据 {}", JSON.toJSONString(requestEntity));
            channel.writeAndFlush(getResponseString(requestEntity));

        } catch (Exception e) {
            throw e;
        }
        return responseEntity;
    }

    private static String getResponseString(MyRequestEntity myRequestEntity) {
        return JSON.toJSONString(myRequestEntity) + "\r\n";
    }

    public void close() {
        log.info("关闭连接");
        if (channel != null && channel.isOpen()) {
            channel.close();
        }
        if (eventLoopGroup != null) {
            eventLoopGroup.shutdownGracefully();
        }

    }


}
