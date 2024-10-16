package com.dd.ddrpc.proxy;

import cn.hutool.core.collection.CollUtil;
import com.dd.ddrpc.RpcApplication;
import com.dd.ddrpc.config.RpcConfig;
import com.dd.ddrpc.constant.RpcConstant;
import com.dd.ddrpc.model.RpcRequest;
import com.dd.ddrpc.model.ServiceMetaInfo;
import com.dd.ddrpc.register.Registry;
import com.dd.ddrpc.register.RegistryFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;

/**
 * 服务代理 （JDK动态代理）
 */
public class ServiceProxy implements InvocationHandler {
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String serviceName = method.getDeclaringClass().getName();
        RpcRequest.builder()
                .serviceName(serviceName)
                .methodName(method.getName())
                .parameterTypes(method.getParameterTypes())
                .args(args)
                .build();

        //从注册中心获取服务提供者的请求地址
        RpcConfig rpcConfig = RpcApplication.getRpcConfig();
        Registry registry = RegistryFactory.getInstance(rpcConfig.getRegistryConfig().getRegistry());
        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setServiceName(serviceName);
        serviceMetaInfo.setServiceVersion(RpcConstant.DEFAULT_SERVICE_VERSION);
        //先从本地缓存获取服务信息 获取不到再从注册中心获取
        List<ServiceMetaInfo> serviceMetaInfoList = registry.serviceDiscovery(serviceMetaInfo.getServiceKey());
        if (CollUtil.isEmpty(serviceMetaInfoList)) {
            throw new RuntimeException("暂无服务地址");
        }


        return null;
    }
}
