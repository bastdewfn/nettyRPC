package com.dewfn.netty.rpc.netty;

import lombok.extern.log4j.Log4j2;
import com.dewfn.netty.rpc.MyResponseEntity;

@Log4j2
public class NettyResponse {

    public NettyResponse(long timeout) {
        this.timeout = timeout;
    }
    public NettyResponse() {
    }


    private Object lock=new Object();
    public void setResponseEntity(MyResponseEntity<String> responseEntity) {


        synchronized (lock) {
            this.responseEntity = responseEntity;
            log.debug("解锁"+Thread.currentThread().getId());
            try{
            lock.notifyAll();}catch(Exception ex) {
                log.warn("解锁异常",ex);
            }
        }
    }

    public void lock(){

    }


    private MyResponseEntity<String> responseEntity=null;
    private long timeout=60*1000*10;
    public MyResponseEntity<String> getResponseEntity() throws InterruptedException {


        log.debug("获取锁"+Thread.currentThread().getId());
        synchronized (lock) {
            if(responseEntity==null) {
                lock.wait(timeout);
                log.debug("获取到了锁" + Thread.currentThread().getId());
            }
            return responseEntity;
        }
    }
}
