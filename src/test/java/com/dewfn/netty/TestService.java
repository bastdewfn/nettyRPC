package com.dewfn.netty;

import com.dewfn.netty.rpc.annotation.MyRpcProduct;

@MyRpcProduct(ServiceName = "testService")
public class TestService implements ITestService {

    @Override
    public TestEntity test(int abc, String d) {
        TestEntity testEntity=new TestEntity();
        testEntity.setId(111000+abc);
        testEntity.setName("远程张三"+d);
        return  testEntity;
    }

    @Override
    public String test2() {
        return "这是一个远程返回的";
    }

    @Override
    public void test3() {
        System.out.println("被调用了空方法");
    }

    @Override
    public void test4(TestEntity entity) {

            System.out.println("被调用了:"+entity);
    }
}
