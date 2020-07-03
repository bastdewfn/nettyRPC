package com.dewfn.netty.rpc.netty.server;

import com.alibaba.fastjson.JSON;
import com.dewfn.netty.rpc.MyResponseEntity;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.log4j.Log4j2;
import com.dewfn.netty.rpc.MyRequestEntity;

import java.nio.charset.Charset;

import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_LENGTH;

@Log4j2
public class ServerLocalCallHandler extends SimpleChannelInboundHandler<MyRequestEntity> {
    //保留所有与服务器建立连接的channel对象，这边的GlobalEventExecutor在写博客的时候解释一下，看其doc
    private static ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MyRequestEntity requestEntity) throws Exception {
        Channel channel=ctx.channel();
        log.info("处理请求:客户端地址{}\n内容：{}",channel.remoteAddress(),JSON.toJSONString(requestEntity));
        channel.eventLoop().execute(()->{
            MyRequestEntity request=requestEntity;
            MyResponseEntity responseEntity =RpcLocalServiceManager.getInstance().localInvoke(request);
            channel.writeAndFlush(responseEntity);

            log.info("响应结束:客户端地址{}\n内容：{}",channel.remoteAddress(),responseEntity);

        });
    }


    //表示服务端与客户端连接建立
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();  //其实相当于一个connection


        channelGroup.add(channel);
        log.info("新的连接:客户端地址:{},当前连接数:{}",channel.remoteAddress(),channelGroup.size());
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();

        log.info("请求离开:客户端地址{}",channel.remoteAddress());
    }

    //连接处于活动状态
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();

        log.info("请求连接:客户端地址{}",channel.remoteAddress());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        log.info("请求断开:客户端地址{}",channel.remoteAddress());

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.warn("netty异常", cause);
        if(ctx!=null) {
            ctx.close();
        }
    }
}
