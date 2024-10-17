package com.dd.ddrpc.proxy;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.dd.ddrpc.RpcApplication;
import com.dd.ddrpc.config.RpcConfig;
import com.dd.ddrpc.constant.RpcConstant;
import com.dd.ddrpc.model.RpcRequest;
import com.dd.ddrpc.model.RpcResponse;
import com.dd.ddrpc.model.ServiceMetaInfo;
import com.dd.ddrpc.register.Registry;
import com.dd.ddrpc.register.RegistryFactory;
import com.dd.ddrpc.serializer.Serializer;
import com.dd.ddrpc.serializer.SerializerFactory;

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
        //TODO 重试机制 容错机制 后续补充
        RpcRequest rpcRequest = RpcRequest.builder().methodName(method.getName()).build();
        Serializer serializer = SerializerFactory.getInstance(RpcApplication.getRpcConfig().getSerializer());
        byte[] bodyBytes = serializer.serialize(rpcRequest);
        //TODO 这里要调用的服务 由 负载均衡算法选出 暂时先取第一位
        RpcResponse rpcResponse = doHttpRequest(serviceMetaInfoList.get(0), bodyBytes);
        return rpcResponse.getData();
    }

    private static RpcResponse doHttpRequest(ServiceMetaInfo selectedServiceMetaInfo, byte[] bodyBytes) throws Exception {
        //获取对应的序列化器
        Serializer serializer = SerializerFactory.getInstance(RpcApplication.getRpcConfig().getSerializer());
        try (HttpResponse httpResponse = HttpRequest.post(selectedServiceMetaInfo.getServiceAddress())
                .body(bodyBytes)
                .execute()) {
            byte[] result = httpResponse.bodyBytes();
            //反序列化
            RpcResponse rpcResponse = serializer.deserialize(result, RpcResponse.class);
            return rpcResponse;
        }
    }
}
