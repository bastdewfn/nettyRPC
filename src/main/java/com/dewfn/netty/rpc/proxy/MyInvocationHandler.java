package com.dewfn.netty.rpc.proxy;

import com.alibaba.fastjson.JSON;
import com.dewfn.netty.rpc.exception.MyRpcExcetipon;
import lombok.extern.log4j.Log4j2;
import com.dewfn.netty.rpc.IMyInvokes;
import com.dewfn.netty.rpc.MyRequestEntity;
import com.dewfn.netty.rpc.MyResponseEntity;
import com.dewfn.netty.rpc.annotation.MyRpcConsume;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Objects;

@Log4j2
public class MyInvocationHandler<T> implements InvocationHandler {
    private IMyInvokes myInvokes;
    private Class<T> tClass;
    public MyInvocationHandler(IMyInvokes myInvokes,Class<T> tClass) {
         this.tClass=tClass;
        this.myInvokes = myInvokes;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        MyRpcConsume rpcConsume = method.getDeclaringClass().getAnnotation(MyRpcConsume.class);
        Objects.requireNonNull(rpcConsume,"请加上MyRpcService注解");


        MyRequestEntity myRequestEntity=new MyRequestEntity();
        myRequestEntity.setClassName(tClass.getName());
        myRequestEntity.setProt(rpcConsume.RemotePort());
        myRequestEntity.setHost(rpcConsume.RemoteIp());
        myRequestEntity.setServiceName(rpcConsume.ServiceName());
        if(args!=null) {
            String[] params = new String[args.length];
            for (int i=0;i<args.length;i++){
                params[i]=JSON.toJSONString(args[i]);
            }
            myRequestEntity.setParams(params);
        }
        myRequestEntity.setRespnseType(method.getReturnType());
        myRequestEntity.setMethodName(method.getName());
        long s= System.currentTimeMillis();
        MyResponseEntity result=myInvokes.invoking(myRequestEntity);
        long e= System.currentTimeMillis()-s;
        if(e>1000){
            log.debug("有点耗时");
        }

        if(result==null)
            throw new MyRpcExcetipon("服务调用失败");
        else if(result.isSuccess()==false){
            throw new MyRpcExcetipon(result.getMsg());
        }

        log.info("远程调用 {}:{} 耗时:{} 类:{},服务名:{}  方法:{}  参数:{} 返回:{} ", rpcConsume.RemoteIp(),
                rpcConsume.RemotePort(),e       ,tClass.getName(),
                rpcConsume.ServiceName(),method.getName(), JSON.toJSONString(args),JSON.toJSONString(result));
        return result.getResult();
    }
}
