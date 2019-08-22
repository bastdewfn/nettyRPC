package com.dewfn.netty.rpc.netty.client.keepAlive;

import com.dewfn.netty.rpc.MyServerInfoEntity;

import java.util.HashMap;
import java.util.Map;

public class RpcClientServiceManager {
    private static RpcClientServiceManager rpcLocalServiceManager=new RpcClientServiceManager();

    public  static RpcClientServiceManager getInstance(){
        return rpcLocalServiceManager;
    }


    private Map<String,RpcKeepAliveClient> clientMap=new HashMap<>();

    public synchronized RpcKeepAliveClient getAvailableClient(MyServerInfoEntity serverInfo) throws InterruptedException {
        RpcKeepAliveClient client=clientMap.getOrDefault(serverInfo.getServiceName(),null);
        if(client==null){
            client=new RpcKeepAliveClient(serverInfo.getHost(),serverInfo.getProt());
            clientMap.put(serverInfo.getServiceName(),client);
        }
        client.run();
        return client;
    }

    public void stop(){
        for (RpcKeepAliveClient client:clientMap.values()){
            client.close();
        }
    }

}
