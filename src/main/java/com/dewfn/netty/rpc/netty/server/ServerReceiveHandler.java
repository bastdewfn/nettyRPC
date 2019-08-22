package com.dewfn.netty.rpc.netty.server;

import com.alibaba.fastjson.JSON;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.log4j.Log4j2;
import com.dewfn.netty.rpc.MyRequestEntity;

@Log4j2
public class ServerReceiveHandler extends SimpleChannelInboundHandler<String> {
    //保留所有与服务器建立连接的channel对象，这边的GlobalEventExecutor在写博客的时候解释一下，看其doc
    private static ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    /**
     * 服务器端收到任何一个客户端的消息都会触发这个方法
     * 连接的客户端向服务器端发送消息，那么其他客户端都收到此消息，自己收到【自己】+消息
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        Channel channel=ctx.channel();
        log.info("收到请求响应:客户端地址{}\n内容：{}",channel.remoteAddress(),msg);

        MyRequestEntity myResponseEntity = JSON.parseObject(msg, MyRequestEntity.class);
        ctx.fireChannelRead(myResponseEntity);
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
