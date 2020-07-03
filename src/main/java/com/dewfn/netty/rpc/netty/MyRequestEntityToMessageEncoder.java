package com.dewfn.netty.rpc.netty;

import com.alibaba.fastjson.JSON;
import com.dewfn.netty.rpc.MyRequestEntity;
import com.dewfn.netty.rpc.MyResponseEntity;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
@Log4j2
public class MyRequestEntityToMessageEncoder extends MessageToMessageEncoder<MyRequestEntity> {

    private static String getRequestString(MyRequestEntity myRequestEntity) {
        return JSON.toJSONString(myRequestEntity) + "\r\n";
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, MyRequestEntity myRequestEntity, List<Object> list) throws Exception {
        String requestString=getRequestString(myRequestEntity);
        log.debug("发送数据,开始转换: {}转换为{}",MyRequestEntity.class,requestString);
        list.add(requestString);
    }
}
