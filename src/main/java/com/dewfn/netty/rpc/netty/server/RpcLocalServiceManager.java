package com.dewfn.netty.rpc.netty.server;

import com.alibaba.fastjson.JSON;
import lombok.extern.log4j.Log4j2;
import com.dewfn.netty.rpc.MyRequestEntity;
import com.dewfn.netty.rpc.MyResponseEntity;
import com.dewfn.netty.rpc.annotation.MyRpcProduct;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Log4j2
public  class RpcLocalServiceManager {
    public static class LocalServiceMethod{

        public LocalServiceMethod(Method method) {
            this.method = method;
            this.paramsClass = method.getParameterTypes();
            try {
                this.sourceObject = method.getDeclaringClass().newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            this.responseClassType=method.getReturnType().getName();
        }

        public Object invoke(String[] params) throws Exception {
            Object[] args=null;
            if(params!=null) {
                args = new Object[params.length];
                for (int i=0;i<params.length;i++){
                    args[i]= JSON.parseObject(params[i],paramsClass[i]);
                }
            }
            Object result=  method.invoke(sourceObject,args);
            return result;


        }
        private Method method;
        private Class<?>[] paramsClass;
        private Object sourceObject;
        private String responseClassType;
    }

    private static RpcLocalServiceManager rpcLocalServiceManager=new RpcLocalServiceManager();

    public  static RpcLocalServiceManager getInstance(){
        return rpcLocalServiceManager;
    }

     Map<String,LocalServiceMethod> localService=new HashMap<>();

    public <T> void  addService(Class<T> interfaceClass,Class<? extends T> serviceClass){
        Method[] methods= serviceClass.getDeclaredMethods();
        String className=serviceClass.getName();
        MyRpcProduct myRpcProduct=serviceClass.getAnnotation(MyRpcProduct.class);
        Objects.requireNonNull(myRpcProduct,"本地服务必需加注解MyRpcProduct");
        for (Method method:methods){

            log.info("添加服务:{},方法:{}",myRpcProduct.ServiceName(),method.getName());
           localService.put(getServiceMethodKey(myRpcProduct.ServiceName(),method.getName())
                   ,new LocalServiceMethod(method));
        }
    }

    public MyResponseEntity<String> localInvoke(MyRequestEntity requestEntity){
        String methodKey=getServiceMethodKey(requestEntity.getServiceName(),requestEntity.getMethodName());
        LocalServiceMethod localServiceMethod= localService.getOrDefault(methodKey,null);
         MyResponseEntity<String> responseEntity=new MyResponseEntity<>();
        responseEntity.setMId(requestEntity.getMId());
         if(localServiceMethod==null){
             responseEntity.setSuccess(false);
             responseEntity.setMsg("404没有此调用方法");
             responseEntity.setCode(404);
             return  responseEntity;
         }

         if( requestEntity.getParams()!=null&&requestEntity.getParams().length!=localServiceMethod.paramsClass.length){
             responseEntity.setSuccess(false);
             responseEntity.setMsg("303请求参数不对应");
             responseEntity.setCode(303);
             return  responseEntity;
         }
        try {
            Object result=localServiceMethod.invoke(requestEntity.getParams());
            responseEntity.setSuccess(true);
            responseEntity.setMsg("200");
            responseEntity.setCode(200);
            responseEntity.setResult(JSON.toJSONString(result));
            responseEntity.setResponseClassType(localServiceMethod.responseClassType);
        } catch (Exception e) {
            log.error("本地调用方法异常:参数:{}",JSON.toJSONString(localServiceMethod),e);
            responseEntity.setSuccess(false);
            responseEntity.setMsg("500 服务内部调用错误");
            responseEntity.setCode(500);
            responseEntity.setResponseClassType(localServiceMethod.responseClassType);
        }

        return  responseEntity;
    }

    private  String getServiceMethodKey(String serviceName,String methodName){
        return serviceName+methodName;
    }

    public void initLocalService(){

    }
}
