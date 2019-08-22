package com.dewfn.netty.rpc;

public interface MyRpcSerializer{
    byte[] serialize(Object var1) ;

    Object deserialize(byte[] var1) ;
}
