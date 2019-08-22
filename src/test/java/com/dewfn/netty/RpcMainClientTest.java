package com.dewfn.netty;

import com.dewfn.netty.rpc.netty.client.ClientConnectionTypeEnum;
import com.dewfn.netty.rpc.proxy.MyProxy;
import org.apache.log4j.PropertyConfigurator;


public class RpcMainClientTest {
    public static void main(String[] args) throws InterruptedException {

        PropertyConfigurator.configure("D:\\MyProject\\threadtest\\src\\main\\java\\log4j2.xml");
        ITestService testService = MyProxy.getSingleProxyObject(ITestService.class, ClientConnectionTypeEnum.Short);



        for (int i=0;i<10;i++) {

            Thread thread = new Thread(() -> {
                for (int j = 0; j < 100; j++) {
                    String r = testService.test2();
                    System.out.println("这是一个远程结果:" + r);
                }
            });
            thread.start();

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
        }

}
