package com.dewfn.netty.rpc;

import lombok.Data;

@Data
public class MyResponseEntity<T> {
    private boolean isSuccess;
    private String msg;
    private int code;
    private T result;
    private String responseClassType;
    private String mId;
}
