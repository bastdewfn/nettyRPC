package com.dewfn.netty;

import com.dewfn.netty.rpc.annotation.MyRpcConsume;

@MyRpcConsume(RemoteIp = "127.0.0.1",RemotePort = 9999,ServiceName = "testService")
public interface ITestService {
    public TestEntity test(int abc, String d);
    public String test2();
    public void test3();
    public void test4(TestEntity entity);
}
