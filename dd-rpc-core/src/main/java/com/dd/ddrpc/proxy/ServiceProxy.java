package com.dd.ddrpc.proxy;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.dd.ddrpc.RpcApplication;
import com.dd.ddrpc.config.RpcConfig;
import com.dd.ddrpc.constant.RpcConstant;
import com.dd.ddrpc.fault.retry.RetryStrategy;
import com.dd.ddrpc.fault.retry.RetryStrategyFactory;
import com.dd.ddrpc.loadbalancer.LoadBalancer;
import com.dd.ddrpc.loadbalancer.LoadBalancerFactory;
import com.dd.ddrpc.model.RpcRequest;
import com.dd.ddrpc.model.RpcResponse;
import com.dd.ddrpc.model.ServiceMetaInfo;
import com.dd.ddrpc.register.Registry;
import com.dd.ddrpc.register.RegistryFactory;
import com.dd.ddrpc.serializer.Serializer;
import com.dd.ddrpc.serializer.SerializerFactory;
import com.dd.ddrpc.service.tcp.VertxTcpClient;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        RpcRequest rpcRequest = RpcRequest.builder().methodName(method.getName()).build();
        Serializer serializer = SerializerFactory.getInstance(RpcApplication.getRpcConfig().getSerializer());
        byte[] bodyBytes = serializer.serialize(rpcRequest);
        //由负载均衡器选择出对应的服务
        LoadBalancer loadBalancer = LoadBalancerFactory.getInstance(rpcConfig.getLoadBalancer());
        // 将调用方法名（请求路径）作为负载均衡参数
        Map<String, Object> requestParams = new HashMap<>();
        requestParams.put("methodName", rpcRequest.getMethodName());
        ServiceMetaInfo selectedServiceMetaInfo = loadBalancer.select(requestParams, serviceMetaInfoList);
        //rpc请求
        //使用重试机制
        RpcResponse rpcResponse = null;
        try {
            RetryStrategy retryStrategy = RetryStrategyFactory.getInstance(rpcConfig.getRetryStrategy());
            retryStrategy.doRetry(() -> VertxTcpClient.doRequest(rpcRequest, selectedServiceMetaInfo));
        } catch (Exception e) {
            //TODO 容错机制 后续补充
            e.printStackTrace();
            rpcResponse = null;
        }
        //RpcResponse rpcResponse = doHttpRequest(selectedServiceMetaInfo, bodyBytes);
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
