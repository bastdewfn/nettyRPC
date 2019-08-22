package com.dewfn.netty.rpc.netty;

import com.dewfn.netty.rpc.MyResponseEntity;

@FunctionalInterface
public interface NettyRpcCallBack {
      void nettyCallBack(MyResponseEntity<String> responseEntity);

}
