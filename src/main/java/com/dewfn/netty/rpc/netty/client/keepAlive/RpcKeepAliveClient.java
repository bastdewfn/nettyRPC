package com.dewfn.netty.rpc.netty.client.keepAlive;

import com.alibaba.fastjson.JSON;
import com.dewfn.netty.rpc.netty.MessageToMyResponseEntityDecoder;
import com.dewfn.netty.rpc.netty.MyRequestEntityToMessageEncoder;
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
import com.dewfn.netty.rpc.netty.client.ClientConnectionStatusEnum;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Log4j2
public class RpcKeepAliveClient {
    private String host;
    private Integer port;

    public ClientConnectionStatusEnum getStatusEnum() {
        return statusEnum;
    }

    public void setStatusEnum(ClientConnectionStatusEnum statusEnum) {
        this.statusEnum = statusEnum;
    }

    private ClientConnectionStatusEnum statusEnum=ClientConnectionStatusEnum.Init;

    public RpcKeepAliveClient(String host, Integer port) {
        this.host = host;
        this.port = port;
    }


    private Channel channel;


    private long timeout = 10000;
    Map<String, NettyResponse> myRequestEntityMap=new ConcurrentHashMap<>();

    public NettyResponse rpcCall(MyRequestEntity requestEntity)  {
        requestEntity.setKeepAlive(true);
        NettyResponse responseEntity = new NettyResponse(timeout);
        if(statusEnum!=ClientConnectionStatusEnum.Open) {
            new Exception("连接已关闭");
        }
            log.info("传送数据 {}", JSON.toJSONString(requestEntity));
            String mId = UUID.randomUUID().toString().replace("-", "");
            requestEntity.setMId(mId);
            myRequestEntityMap.put(mId, responseEntity);
            channel.writeAndFlush(getResponseString(requestEntity));

        return responseEntity;

    }
    EventLoopGroup eventLoopGroup;
    public void run() throws InterruptedException {

        if(statusEnum==ClientConnectionStatusEnum.Init||statusEnum==ClientConnectionStatusEnum.Close) {
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
                                pipeline.addLast(new MyRequestEntityToMessageEncoder());
                                pipeline.addLast(new MessageToMyResponseEntityDecoder());
                                pipeline.addLast(new ClinetReceiveKeepAliveHandler((response) -> {
                                    if(response.getMId()==null){
                                        myRequestEntityMap.values().forEach(x->{ x.setResponseEntity(response); });
                                        myRequestEntityMap.clear();
                                    }else {
                                        NettyResponse responseEntity = myRequestEntityMap.getOrDefault(response.getMId(), null);
                                        if (responseEntity != null) {
                                            myRequestEntityMap.remove(response.getMId());
                                            responseEntity.setResponseEntity(response);
                                        }
                                    }
                                }));
                            }

                            ;
                        });

                log.debug("连接打开 ");
                ChannelFuture channelFuture = bootstrap.connect(host, port);
                // responseEntity.lock();
                channel = channelFuture.sync().channel();
                statusEnum=ClientConnectionStatusEnum.Open;

            } catch (Exception e) {
                throw e;
            }
        }
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
        statusEnum=ClientConnectionStatusEnum.Close;

    }


}
