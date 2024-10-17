package com.dd.ddrpc.bootstrap;

import com.dd.ddrpc.RpcApplication;
import com.dd.ddrpc.config.RpcConfig;
import com.dd.ddrpc.model.ServiceMetaInfo;
import com.dd.ddrpc.model.ServiceRegisterInfo;
import com.dd.ddrpc.register.LocalRegistry;
import com.dd.ddrpc.register.Registry;
import com.dd.ddrpc.register.RegistryFactory;

import java.util.List;

/**
 * 服务提供者启动类（初始化）
 */
public class ProviderBootstrap {

    /**
     * 初始化
     * @param serviceRegisterInfoList
     */
    public static void init(List<ServiceRegisterInfo<?>> serviceRegisterInfoList) {
        //RPC框架初始化 (配置和注册中心)
        RpcApplication.init();

        //获取全局配置
        final RpcConfig rpcConfig = RpcApplication.getRpcConfig();
        //注册服务
        for (ServiceRegisterInfo<?> serviceRegisterInfo : serviceRegisterInfoList) {
            //注册到本地服务缓存
            String serviceName = serviceRegisterInfo.getServiceName();
            LocalRegistry.register(serviceName, serviceRegisterInfo.getImplClass());

            //获取注册中心
            Registry registry = RegistryFactory.getInstance(rpcConfig.getRegistryConfig().getRegistry());
            //构造注册服务信息
            ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
            serviceMetaInfo.setServiceName(serviceName);
            serviceMetaInfo.setServiceHost(rpcConfig.getServerHost());
            serviceMetaInfo.setServicePort(rpcConfig.getServerPort());
            try {
                registry.register(serviceMetaInfo);
            } catch (Exception e) {
                throw new RuntimeException(serviceName + "服务注册失败" + e);
            }
        }

        // TODO 启动服务器

    }
}
