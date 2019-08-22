package com.dewfn.netty.rpc.proxy;

import com.dewfn.netty.rpc.IMyInvokes;
import com.dewfn.netty.rpc.MyRpcInvokes;
import com.dewfn.netty.rpc.MyRpcKeepAliveInvokes;
import com.dewfn.netty.rpc.netty.client.ClientConnectionTypeEnum;

import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MyProxy<T> {


    public T getProxyObject(Class<T> serviceInterface, ClientConnectionTypeEnum typeEnum) {
        IMyInvokes myRpcInvokes=null;
        switch (typeEnum){
            case Short:
                myRpcInvokes=new MyRpcInvokes();
                break;
            case KeepAlive:
                myRpcInvokes=new MyRpcKeepAliveInvokes();
                break;
        }
            return (T) Proxy.newProxyInstance(serviceInterface.getClassLoader(), new Class[]{serviceInterface},
                    new MyInvocationHandler<T>(myRpcInvokes,serviceInterface));
    }
    private  static Map<Class,Object> proxyList=new ConcurrentHashMap<>();

    public static <T> T  getSingleProxyObject( Class<T> serviceInterface,ClientConnectionTypeEnum typeEnum){
        Object proxy=proxyList.getOrDefault(serviceInterface,null);
        if(proxy==null) {
            proxy=   new MyProxy<T>().getProxyObject(serviceInterface,typeEnum);
            proxyList.put(serviceInterface,proxy);
        }
        return (T) proxy;
    }

}
