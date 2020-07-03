package com.dewfn.netty;

import com.dewfn.netty.rpc.netty.server.RpcLocalServiceManager;
import com.dewfn.netty.rpc.netty.server.RpcServer;
import lombok.extern.log4j.Log4j2;
import org.apache.log4j.PropertyConfigurator;


@Log4j2
public class RpcMainServerTest {
    public static void main(String[] args){

        PropertyConfigurator.configure("D:\\MyProject\\threadtest\\src\\main\\java\\log4j2.xml");
        RpcLocalServiceManager.getInstance().addService(ITestService.class,TestService.class);

        try {
             RpcServer.run();
        } catch (Exception e) {
            log.error("开户监听失败",e);
        }
    }
}
