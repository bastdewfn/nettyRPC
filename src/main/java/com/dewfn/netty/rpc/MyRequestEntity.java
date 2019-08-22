package com.dewfn.netty.rpc;

import lombok.Data;

@Data
public class MyRequestEntity extends  MyServerInfoEntity{

    private String className;
    private String methodName;
    private String[] params;
    private Class<?> respnseType;
    private String mId;
    private boolean keepAlive;
}
