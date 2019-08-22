package com.dewfn.netty.rpc.annotation;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MyRpcConsume {
    String ServiceName();
    String RemoteIp();
    int RemotePort();
}
