package com.dewfn.netty;

import com.dewfn.netty.rpc.netty.client.ClientConnectionTypeEnum;
import com.dewfn.netty.rpc.netty.client.keepAlive.RpcClientServiceManager;
import com.dewfn.netty.rpc.proxy.MyProxy;
import org.apache.log4j.PropertyConfigurator;


import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class RpcMainClientKeepTest {
    public static void main(String[] args) throws InterruptedException {

        PropertyConfigurator.configure("D:\\MyProject\\threadtest\\src\\main\\java\\log4j2.xml");

//        for (int i=0;i<2;i++) {
//            ITestService testService = MyProxy.getSingleProxyObject(ITestService.class, ClientConnectionTypeEnum.KeepAlive);
//
//
//            TestEntity r = testService.test(i,"test");
//
//            System.out.println("这是一个远程结果:" + r);
//
//           // testService.test(1, "t");
//        }



//
        long s=System.currentTimeMillis();
        for (int i=0;i<1000;i++) {

            Thread thread = new Thread(() -> {
                while (true) {
                    if(System.currentTimeMillis()>s+10000){break;}
                    ITestService testService = MyProxy.getSingleProxyObject(ITestService.class, ClientConnectionTypeEnum.KeepAlive);

                    TestEntity r = testService.test(1, "test");
                    System.out.println("这是一个远程结果:" + r);

                }
            });
            thread.start();
        }
        try {
            CompletableFuture.runAsync(()->{
                try {
                    Thread.sleep(20000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                RpcClientServiceManager.getInstance().stop();
            }).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }


//            TestEntity t = testService.test(1, "测试");
//            System.out.println("这是一个远程结果:" + t);
//
//            testService.test3();
//            System.out.println("这是一个远程结果:");
//
//            TestEntity testEntity = new TestEntity();
//            testEntity.setName("本地");
//            testEntity.setId(2);
//            testService.test4(testEntity);
//            System.out.println("这是一个远程结果:");
       // RpcClientServiceManager.getInstance().stop();
        }

}
