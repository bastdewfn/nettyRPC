package com.dewfn.netty.rpc;

public interface IMyInvokes {
     <T> MyResponseEntity<T>  invoking(MyRequestEntity requestEntity);
}
