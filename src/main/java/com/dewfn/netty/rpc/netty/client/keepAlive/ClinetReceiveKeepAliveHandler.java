package com.dewfn.netty.rpc.netty.client.keepAlive;

import com.alibaba.fastjson.JSON;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.log4j.Log4j2;
import com.dewfn.netty.rpc.MyResponseEntity;
import com.dewfn.netty.rpc.netty.NettyRpcCallBack;

@Log4j2
public class ClinetReceiveKeepAliveHandler extends SimpleChannelInboundHandler<String> {

    public ClinetReceiveKeepAliveHandler(NettyRpcCallBack rpcCallBack) {
        this.rpcCallBack = rpcCallBack;
    }

    private NettyRpcCallBack rpcCallBack;
    private boolean isEnd;



    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {

        log.debug("channelRegistered");

    }

    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        log.debug("channelUnregistered");
    }

    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.debug("channelActive");
    }

    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.debug("channelInactive");
        if(!isEnd){
            isEnd=true;
            MyResponseEntity responseEntity=new MyResponseEntity();
            responseEntity.setMsg("异常关闭");
            responseEntity.setSuccess(false);
            rpcCallBack.nettyCallBack(responseEntity);
        }
        ctx.fireChannelInactive();
    }

    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String o) throws Exception {

        log.info("netty返回:"+o);
        if(!isEnd) {
            MyResponseEntity<String> myResponseEntity = JSON.parseObject(o, MyResponseEntity.class);
            rpcCallBack.nettyCallBack(myResponseEntity);
        }

    }

    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        log.debug("channelReadComplete");
    }

    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        log.debug("userEventTriggered");
    }

    public void channelWritabilityChanged(ChannelHandlerContext ctx)  throws Exception {
        log.debug("channelWritabilityChanged");
    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if(!isEnd){
            isEnd=true;
            MyResponseEntity responseEntity=new MyResponseEntity();
        responseEntity.setMsg("本地内部错误");
        responseEntity.setSuccess(false);
        rpcCallBack.nettyCallBack(responseEntity);
        }
        log.warn("netty异常",  cause);


    }
}
