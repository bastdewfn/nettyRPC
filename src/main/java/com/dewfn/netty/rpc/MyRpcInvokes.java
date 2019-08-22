package com.dewfn.netty.rpc;

import com.alibaba.fastjson.JSON;
import lombok.extern.log4j.Log4j2;
import com.dewfn.netty.rpc.netty.NettyResponse;
import com.dewfn.netty.rpc.netty.client.RpcClient;

@Log4j2
public class MyRpcInvokes implements IMyInvokes {

    @Override
    public <T> MyResponseEntity<T> invoking(MyRequestEntity requestEntity) {
        RpcClient rpcClient=new RpcClient(requestEntity);
        MyResponseEntity<T> realResponse = new MyResponseEntity<>();

        MyResponseEntity<String> responseEntity=null;
        try {
            NettyResponse  response = rpcClient.rpcCall();


            responseEntity= response.getResponseEntity();
        } catch (Exception e) {
            log.error("调用Client异常",e);
        }finally {
            rpcClient.close();
        }
        log.debug("调用响应:"+responseEntity);
        if(responseEntity!=null) {


            realResponse.setSuccess(responseEntity.isSuccess());
            realResponse.setMsg(responseEntity.getMsg());
            realResponse.setResponseClassType(responseEntity.getResponseClassType());

            T t= (T) JSON.parseObject(responseEntity.getResult(),requestEntity.getRespnseType());
            realResponse.setResult(t);
            return realResponse;
        }
        realResponse.setSuccess(false);
        realResponse.setMsg("没有请求成功");

        return realResponse;
    }
}
