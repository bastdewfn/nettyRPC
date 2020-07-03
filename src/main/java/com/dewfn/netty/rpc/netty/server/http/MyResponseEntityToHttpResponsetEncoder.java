package com.dewfn.netty.rpc.netty.server.http;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.dewfn.netty.rpc.MyRequestEntity;
import com.dewfn.netty.rpc.MyResponseEntity;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.http.*;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;
import java.util.List;

import static io.netty.handler.codec.http.HttpHeaderNames.CONNECTION;

@Slf4j
public class MyResponseEntityToHttpResponsetEncoder extends MessageToMessageEncoder<MyResponseEntity> {


    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, MyResponseEntity myResponseEntity, List<Object> list) throws Exception {

        FullHttpResponse response =null;
        if(myResponseEntity.isSuccess()==false){
            if(myResponseEntity.getCode()==0)
                myResponseEntity.setCode(500);
            response=   new DefaultFullHttpResponse(
                    HttpVersion.HTTP_1_1,
                    HttpResponseStatus.valueOf(myResponseEntity.getCode()),
                    Unpooled.wrappedBuffer(JSON.toJSONString(myResponseEntity.getMsg()).getBytes(Charset.defaultCharset())));
        }else {
            response = new DefaultFullHttpResponse(
                    HttpVersion.HTTP_1_1,
                    HttpResponseStatus.OK,
                    Unpooled.wrappedBuffer(myResponseEntity.getResult().toString().getBytes(Charset.defaultCharset())));
        }
        response.headers().set("Content-Type", "application/json;charset=utf-8");
        response.headers().set("Content-Length", response.content().readableBytes());
        list.add(response);
    }
    public void flush(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }
}
