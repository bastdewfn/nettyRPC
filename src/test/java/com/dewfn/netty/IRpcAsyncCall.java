package com.dewfn.netty;

import com.dewfn.netty.rpc.annotation.MyRpcConsume;
import io.netty.util.concurrent.Future;

import java.util.function.Consumer;
import java.util.function.Supplier;

public interface IRpcAsyncCall {
    public void AsyncDecorate(Consumer<Future> futureSupplier);
}
