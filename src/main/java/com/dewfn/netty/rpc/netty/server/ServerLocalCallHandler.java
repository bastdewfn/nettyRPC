package com.dewfn.netty.rpc.netty.server;

import com.alibaba.fastjson.JSON;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.log4j.Log4j2;
import com.dewfn.netty.rpc.MyRequestEntity;
import com.dewfn.netty.rpc.MyResponseEntity;

@Log4j2
public class ServerLocalCallHandler extends SimpleChannelInboundHandler<MyRequestEntity> {

    /**
     * 服务器端收到任何一个客户端的消息都会触发这个方法
     * 连接的客户端向服务器端发送消息，那么其他客户端都收到此消息，自己收到【自己】+消息
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MyRequestEntity requestEntity) throws Exception {
        Channel channel=ctx.channel();
        log.info("处理请求:客户端地址{}\n内容：{}",channel.remoteAddress(),requestEntity);
       channel.eventLoop().execute(()->{
            MyRequestEntity request=requestEntity;
            MyResponseEntity<String> responseEntity =RpcLocalServiceManager.getInstance().localInvoke(request);
                channel.writeAndFlush(getResponseString(responseEntity));

         log.info("响应结束:客户端地址{}\n内容：{}",channel.remoteAddress(),responseEntity);
       });

    }
    private String getResponseString(MyResponseEntity<String> myResponseEntity){
        return JSON.toJSONString(myResponseEntity)+"\r\n";
    }


}
