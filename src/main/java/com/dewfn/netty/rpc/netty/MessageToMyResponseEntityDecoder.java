package com.dewfn.netty.rpc.netty;

import com.alibaba.fastjson.JSON;
import com.dewfn.netty.rpc.MyRequestEntity;
import com.dewfn.netty.rpc.MyResponseEntity;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
@Log4j2
public class MessageToMyResponseEntityDecoder extends MessageToMessageDecoder<String> {

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, String s, List<Object> list) throws Exception {
        log.debug("收到数据,开始转换:转换为:{} 内容：{}",MyResponseEntity.class,s);
        try{
        MyResponseEntity myResponseEntity = JSON.parseObject(s, MyResponseEntity.class);
        list.add(myResponseEntity);}catch (Exception ex){ex.printStackTrace();}
    }
}
