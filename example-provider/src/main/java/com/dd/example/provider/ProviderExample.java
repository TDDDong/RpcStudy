package com.dd.example.provider;

import com.dd.ddrpc.bootstrap.ProviderBootstrap;
import com.dd.ddrpc.model.ServiceRegisterInfo;
import com.dd.example.common.service.UserService;

import java.util.ArrayList;
import java.util.List;

/**
 * 服务提供者示例
 */
public class ProviderExample {
    public static void main(String[] args) {
        //构建要注册的服务类列表
        List<ServiceRegisterInfo<?>> serviceRegisterInfoList = new ArrayList<>();
        ServiceRegisterInfo<UserService> serviceRegisterInfo = new ServiceRegisterInfo<>(UserService.class.getName(), UserServiceImpl.class);
        serviceRegisterInfoList.add(serviceRegisterInfo);

        //服务提供者初始化
        ProviderBootstrap.init(serviceRegisterInfoList);
    }
}
