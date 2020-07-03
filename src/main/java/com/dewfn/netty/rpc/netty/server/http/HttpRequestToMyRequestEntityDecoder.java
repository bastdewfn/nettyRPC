package com.dewfn.netty.rpc.netty.server.http;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.dewfn.netty.rpc.MyRequestEntity;
import com.dewfn.netty.rpc.MyResponseEntity;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.*;
import io.netty.util.concurrent.GlobalEventExecutor;
import io.netty.util.internal.StringUtil;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;
import java.util.List;

@Log4j2
public class HttpRequestToMyRequestEntityDecoder extends SimpleChannelInboundHandler<FullHttpRequest> {



    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, FullHttpRequest fullHttpRequest) throws Exception {

        String url=fullHttpRequest.uri();
        String content= fullHttpRequest.content().toString(Charset.defaultCharset());
        log.info("处理请求:客户端地址{}\n请求URL：{},请求内容:{}", fullHttpRequest.uri(),content);
        MyRequestEntity request=new MyRequestEntity();
        try {
            String[] urlP = url.split("/");

//            request.setClassName();
            request.setMethodName(urlP[urlP.length - 1]);
            request.setServiceName(urlP[urlP.length - 2]);
            request.setKeepAlive(false);

            JSONArray jsonParams = StringUtil.isNullOrEmpty(content)?new JSONArray(): JSON.parseArray(content);
            String[] params = new String[jsonParams.size()];
            for (int i = 0; i < jsonParams.size(); i++) {
                params[i] = JSON.toJSONString(jsonParams.get(i));
            }
            request.setParams(params);

        }catch (Exception ex){
            log.error("转换HTTP数据异常",ex);

        }
        channelHandlerContext.fireChannelRead(request);
    }

}
